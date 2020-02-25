package ru.skillbranch.skillarticles.extensions

import android.util.Log

//Реализуй функцию расширения fun String.indexesOf(substr: String, ignoreCase: Boolean = true): List,
// в качестве аргумента принимает подстроку и флаг - учитывать или нет регистр подстроки при поиске
// по исходной строке. Возвращает список позиций вхождений подстроки в исходную строку.
// Пример: "lorem ipsum sum".indexesOf("sum") // [8, 12]


fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int>{
    if (this==null) return mutableListOf<Int>()
    //Log.e("Debug","handleSearch String.indexesOf="+this)
    //if (this.isNullOrEmpty()) return mutableListOf<Int>()
    var i:Int=0
    var k:Int=-1
    var lst= mutableListOf<Int>()
    //Log.e("Debug","io_index esOf start ")
    //Log.e("Debug","String.indexesOf $this $substr ")
    //k=curStr.indexOf(substr,i,ignoreCase)
    //lst.add(k)
    //Log.e("Debug","String.indexesOf $k $substr ")
    //Log.e("Debug","handleSearch String.indexesOf before while")
    while (this.indexOf(substr,i,ignoreCase)!=-1) {
        k=this.indexOf(substr,i,ignoreCase)
        //Log.e("Debug","io_index circle step $k ")
        if (k!=-1) {
            lst.add(k)
            i=k+1
             //Log.e("Debug","io_index $k ")
        }
    }
    //Log.e("Debug","handleSearch String.indexesOf after while")
    return  lst
    //return  mutableListOf<Int>()
}