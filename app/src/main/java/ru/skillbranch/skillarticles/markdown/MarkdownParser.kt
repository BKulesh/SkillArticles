package ru.skillbranch.skillarticles.markdown

import java.util.regex.Pattern

object MarkdownParser {
    private val LINE_SEPARATOR=System.getProperty("line.separator") ?: "\n"

    private const val UNORDERED_LIST_ITEM_GROUP = ""

    const val MARKDOWN_GROUPS="$UNORDERED_LIST_ITEM_GROUP"

    private val elementsPatten by lazy{Pattern.compile(MARKDOWN_GROUPS,Pattern.MULTILINE)}

    fun parse(string: String): MarkdownText{
        val elements= mutableListOf<Element>()

        elements.addAll(findElements(string))
        return MarkdownText(elements)
    }

    fun clear(string: String) : String?{

    }

    private fun findElements(string: CharSequence): List<Element> {

    }
}

data class MarkdownText(val elements: List<Element>)

sealed class Element(){
    abstract val text: CharSequence
    abstract val elements: List<Element>
}