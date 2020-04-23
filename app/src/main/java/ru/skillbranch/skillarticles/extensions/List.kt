package ru.skillbranch.skillarticles.extensions

import android.util.Log

fun List<Pair<Int,Int>>.groupByBounds(bounds: List<Pair<Int,Int>>):List<List<Pair<Int,Int>>>{
    //return listOf(listOf(0 to 0,1 to 1))
    var resultList=emptyList<List<Pair<Int,Int>>>()
    var pairList:List<Pair<Int,Int>>
  bounds.forEach{curBound->
      pairList=emptyList<Pair<Int,Int>>()
      Log.e("Debug","groupByBounds curBound "+curBound.first.toString()+","+curBound.second.toString())
      this.forEach{resSearchPair->
          Log.e("Debug","groupByBounds resSearchPair "+resSearchPair.first.toString()+","+resSearchPair.second.toString())
          if ((curBound.first<=resSearchPair.first) && (curBound.second>=resSearchPair.second)) {
              //Log.e("Debug","groupByBounds resSearchPair!!! "+resSearchPair.first.toString()+","+resSearchPair.second.toString())
              //pairList.contains(resSearchPair.first to resSearchPair.second)
              pairList+=(resSearchPair.first to resSearchPair.second)
          }
          //if (pairList.count()>0) resultList.plusElement(pairList)
          //Log.e("Debug","groupByBounds pairList.count()="+pairList.count().toString())
      }
      //Log.e("Debug","groupByBounds pairList.count()="+pairList.count().toString())
      resultList=resultList.plusElement(pairList)
      //Log.e("Debug","groupByBounds resultList.count()="+resultList.count().toString())
  }
      return resultList
}