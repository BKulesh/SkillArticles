package ru.skillbranch.skillarticles.markdown

import android.util.Log
import java.util.regex.Pattern

object MarkdownParser {
    private val LINE_SEPARATOR=System.getProperty("line.separator") ?: "\n"

    private const val UNORDERED_LIST_ITEM_GROUP = "(^[*+-] .+$)"
    private const val HEADER_GROUP = "(^#{1,6} .+?$)"
    private const val QUOTE_GROUP = "(^> .+?$)"
    private const val ITALIC_GROUP = "((?<!\\*)\\*[^*].*?[^*]?\\*(?!\\*)|(?<!_)_[^_].*?[^_]?_(?!_))"
    private const val BOLD_GROUP = "((?<!\\*)\\*{2}[^*].*?[^*]?\\*{2}(?!\\*)|(?<!_)_{2}[^_].*?[^_]?_{2}(?!_))"
    private const val STRIKE_GROUP = "((?<!\\~)\\~{2}[^~].*?[^~]?\\~{2}(?!\\~))"
    private const val RULE_GROUP = "(^[_*-]{3}$)"
    private const val INLINE_GROUP = "((?<!`)`[^`\\s].*?[^`\\s]?`(?!`))"
    private const val LINK_GROUP = "(\\[[^\\[\\]]*?]\\(.+?\\)|^\\[*?]\\(.*?\\))"

    private const val MARKDOWN_GROUPS="$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP|$QUOTE_GROUP|$ITALIC_GROUP|$BOLD_GROUP|$STRIKE_GROUP|$RULE_GROUP|$INLINE_GROUP|$LINK_GROUP"

    private val elementsPatten by lazy{Pattern.compile(MARKDOWN_GROUPS,Pattern.MULTILINE)}

    fun parse(string: String): MarkdownText{
        val elements= mutableListOf<Element>()

        elements.addAll(findElements(string))
        return MarkdownText(elements)
    }

    fun clear(string: String?) : String?{
        return null
    }

    private fun findElements(string: CharSequence): List<Element> {
        val parents= mutableListOf<Element>()
        val matcher = elementsPatten.matcher(string)
        var lastStartIndex=0

        Log.e("Debug","findElements START $string ")

        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex=matcher.start()
            val endIndex=matcher.end()

            if(lastStartIndex < startIndex){
                parents.add(Element.Text(string.subSequence(lastStartIndex,startIndex)))
            }

            var text: CharSequence

            val groups=1..9
            var group=-1
            for(gr in groups){
                if(matcher.group(gr)!=null){
                    group=gr
                    break
                }
            }

            Log.e("Debug"," parse_group $group ")

            when(group) {
                -1 -> break@loop
                1->{
                    text=string.subSequence(startIndex.plus(2),endIndex)

                    val subs= findElements(text)
                    val element=Element.UnorderedListItem(text,subs)
                    Log.e("Debug"," parse_UnorderedListItem $text ")
                    parents.add(element)

                    lastStartIndex=endIndex
                }
                2->{
                    val reg="^#{1,6}".toRegex().find(string.subSequence(startIndex,endIndex))
                    val level=reg!!.value.length
                    text=string.subSequence(startIndex.plus(level.inc()),endIndex)
                    val element=Element.Header(level,text)
                    //Log.e("Debug"," parse_Qoute $text ")
                    parents.add(element)
                    lastStartIndex=endIndex
                }
                3->{
                    //val reg="^>".toRegex().find(string.subSequence(startIndex,endIndex))
                    //val level=reg!!.value.length
                    text=string.subSequence(startIndex.plus(2),endIndex)
                    val subelements=findElements(text)

                    val element=Element.Quote(text,subelements)
                    Log.e("Debug"," parse_Qoute $text ")
                    parents.add(element)
                    lastStartIndex=endIndex
                }
                4->{
                    text=string.subSequence(startIndex.inc(),endIndex.dec())
                    val subelements=findElements(text)

                    val element=Element.Italic(text,subelements)
                    parents.add(element)
                    lastStartIndex=endIndex
                }
                5->{
                    text=string.subSequence(startIndex.plus(2),endIndex.plus(-2))
                    val subelements=findElements(text)

                    val element=Element.Bold(text,subelements)
                    parents.add(element)
                    lastStartIndex=endIndex
                }
                6->{
                    text=string.subSequence(startIndex.plus(2),endIndex.plus(-2))
                    val subelements=findElements(text)

                    val element=Element.Strike(text,subelements)
                    parents.add(element)
                    lastStartIndex=endIndex
                }
                7->{
                    val element=Element.Rule()
                    parents.add(element)
                    lastStartIndex=endIndex
                }
                8->{
                    text=string.subSequence(startIndex.inc(),endIndex.dec())

                    val element=Element.InlineCode(text)
                    parents.add(element)
                    lastStartIndex=endIndex

                }
                9->{
                    text=string.subSequence(startIndex,endIndex)
                    val (title: String, link : String)="\\[(.*)]\\((.*)\\)".toRegex().find(text)!!.destructured
                    val element=Element.Link(text=title,link=link)
                    parents.add(element)
                    lastStartIndex=endIndex

                }
            }

        }

        if (lastStartIndex < string.length) {
            val text=string.subSequence(lastStartIndex,string.length)
            parents.add(Element.Text(text))
        }

        return parents
    }
}

data class MarkdownText(val elements: List<Element>)

sealed class Element(){
    abstract val text: CharSequence
    abstract val elements: List<Element>

    data class Text(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class UnorderedListItem(
     override val text: CharSequence,
     override val elements: List<Element> = emptyList()
    ): Element()

    data class Header(
        val level: Int=1,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class Quote(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class Italic(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class Bold(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class Strike(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class Rule(
        override val text: CharSequence=" ",
        override val elements: List<Element> = emptyList()
    ): Element()

    data class InlineCode(
        override val text: CharSequence=" ",
        override val elements: List<Element> = emptyList()
    ): Element()

    data class Link(
        val link: String,
        override val text: CharSequence=" ",
        override val elements: List<Element> = emptyList()
    ): Element()

}