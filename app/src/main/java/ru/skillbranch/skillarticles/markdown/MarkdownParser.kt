package ru.skillbranch.skillarticles.markdown

import java.util.regex.Pattern

object MarkdownParser {
    private val LINE_SEPARATOR=System.getProperty("line.separator") ?: "\n"

    private const val UNORDERED_LIST_ITEM_GROUP = "(^[*+-] .+$)"
    private const val HEADER_GROUP = "(^#{1,6} .+?$)"

    private const val MARKDOWN_GROUPS="$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP"

    private val elementsPatten by lazy{Pattern.compile(MARKDOWN_GROUPS,Pattern.MULTILINE)}

    fun parse(string: String): MarkdownText{
        val elements= mutableListOf<Element>()

        elements.addAll(findElements(string))
        return MarkdownText(elements)
    }

    fun clear(string: String) : String?{
        return null
    }

    private fun findElements(string: CharSequence): List<Element> {
        val parents= mutableListOf<Element>()
        val matcher = elementsPatten.matcher(string)
        var lastStartIndex=0

        loop@while (matcher.find(lastStartIndex)) {
            val startIndex=matcher.start()
            val endIndex=matcher.end()

            if(lastStartIndex<startIndex){
                parents.add(Element.Text(string.subSequence(lastStartIndex,startIndex)))
            }

            var text: CharSequence

            val groups=1..2
            var group=-1
            for(gr in groups){
                if(matcher.group(gr)!=null){
                    group=gr
                    break
                }
            }

            when(group) {
                -1 -> break@loop
                1->{
                    text=string.subSequence(startIndex.plus(2),endIndex)

                    val subs= findElements(text)
                    val element=Element.UnorderedListItem(text,subs)
                    parents.add(element)

                    lastStartIndex=endIndex
                }
                2->{
                    val reg="^#{1,6}".toRegex().find(string.subSequence(startIndex,endIndex))
                    val level=reg!!.value.length
                    text=string.subSequence(startIndex.plus(level.inc()),endIndex)
                    val element=Element.Header(level,text)
                    parents.add(element)
                    lastStartIndex=endIndex
                }
            }

            if (lastStartIndex<string.length) {
                val text=string.subSequence(lastStartIndex,string.length)
                parents.add(Element.Text(text))
            }

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

}