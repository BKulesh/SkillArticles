package ru.skillbranch.skillarticles

import android.util.Log
import org.junit.Test

import org.junit.Assert.*
import ru.skillbranch.skillarticles.extensions.indexesOf
import ru.skillbranch.skillarticles.markdown.Element
import ru.skillbranch.skillarticles.markdown.MarkdownParser

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
   @Test
   fun parse_list_item(){
        val result=MarkdownParser.parse(unorderedListString)
       val actual = prepare<Element.UnorderedListItem>(result.elements)
       assertEquals(expectedUnorderedList,actual)

       printResults(actual)
       println("")
       printElements(result.elements)

   }


    @Test
    fun parse_header(){
        val result=MarkdownParser.parse(headerString)
        val actual = prepare<Element.Header>(result.elements)
        assertEquals(expectedHeader,actual)

        printResults(actual)
        println("")
        printElements(result.elements)

    }

    private fun printResults(list: List<String>)
    {
        val iterator=list.iterator()
        while (iterator.hasNext()) {
            print ("find >>  ${iterator.next()}")
        }
    }

    private fun printElements(list: List<Element>)
    {
        val iterator=list.iterator()
        while (iterator.hasNext()) {
            print ("element >>  ${iterator.next()}")
        }
    }

    private fun Element.spread(): List<Element>{
        val elements= mutableListOf<Element>()
        elements.add(this)
        elements.addAll(this.elements.spread())
        return elements
    }

    private fun List<Element>.spread():List<Element>{
        val elements= mutableListOf<Element>()

        if (this.isNotEmpty()) elements.addAll(
            this.fold(mutableListOf()){acc,el->acc.also { it.addAll(el.spread()) }}
        )
        return elements
    }

    private inline fun<reified T:Element> prepare(list: List<Element>): List<String>{
        return list
            .fold(mutableListOf<Element>()){ acc,el -> acc.also{ it.addAll(el.spread()) }
            }
            .filterIsInstance<T>()
            .map { it.text.toString() }
    }

}
