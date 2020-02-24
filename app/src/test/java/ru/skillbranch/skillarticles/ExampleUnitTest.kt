package ru.skillbranch.skillarticles

import android.util.Log
import org.junit.Test

import org.junit.Assert.*
import ru.skillbranch.skillarticles.extensions.indexesOf

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun test_indexesof(){

        var str="test la mest ura la la ego"
        /*println(str)
        println(str.indexOf("xx",0,true))
        println(str.indexOf("Test",0,true))
        println(str.indexOf("Test",0,false))
        val substr="la"
        var k=-1
        var i=0
        while (str.indexOf(substr,i,true)!=-1) {
            k=str.indexOf(substr,i,true)
            println("1. i="+i)
            if (k!=-1) {
                //str=str.substring(k+substr.length)
                i=k+1
                println("2. k="+k)
            }
        }*/
        //println("2. k="+k)
        val lst=str.indexesOf("la")
        println("before")
        lst.forEach { println(it.toString()) }
        println("after")

    }
}
