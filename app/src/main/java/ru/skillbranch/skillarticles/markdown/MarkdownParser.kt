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
    private const val WRAP_BLOCK_CODE_GROUP = "(^[\\`]{3}.*[\\`]{3}\$)"
    //private const val MULTILINE_WRAP_BLOCK_CODE_GROUP = "(^[\\`]{3}[\\s,\\S]*[\\`]{3}\$)"
    private const val MULTILINE_WRAP_BLOCK_CODE_GROUP = "(^[\\`]{3}[^\\`]*[\\`]{3}\$)"
    private const val ORDERED_LIST_ITEM_GROUP = "(^[\\d]*\\..*\$)"



    private const val MARKDOWN_GROUPS="$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP|$QUOTE_GROUP|$ITALIC_GROUP|$BOLD_GROUP|$STRIKE_GROUP|$RULE_GROUP|$INLINE_GROUP|$LINK_GROUP|$ORDERED_LIST_ITEM_GROUP|$MULTILINE_WRAP_BLOCK_CODE_GROUP"

    private val elementsPatten by lazy{Pattern.compile(MARKDOWN_GROUPS,Pattern.MULTILINE)}

    fun parse(string: String): MarkdownText{
        val elements= mutableListOf<Element>()
        //val cs1= clear(string)
        //val cs: CharSequence=cs1!!.subSequence(0,cs1!!.length)
        //elements.addAll(findElements(cs))
        elements.addAll(findElements(string))
        return MarkdownText(elements)
    }


    private fun findElements(string: CharSequence): List<Element> {
        val parents= mutableListOf<Element>()
        val matcher = elementsPatten.matcher(string)
        var lastStartIndex=0

        //Log.e("Debug","findElements START $string ")

        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex=matcher.start()
            val endIndex=matcher.end()

            if(lastStartIndex < startIndex){
                parents.add(Element.Text(string.subSequence(lastStartIndex,startIndex)))
            }

            var text: CharSequence

            val groups=1..11
            var group=-1
            for(gr in groups){
                if(matcher.group(gr)!=null){
                    group=gr
                    break
                }
            }

            //Log.e("Debug"," parse_group $group ")

            when(group) {
                -1 -> break@loop
                1->{
                    text=string.subSequence(startIndex.plus(2),endIndex)

                    val subs= findElements(text)
                    val element=Element.UnorderedListItem(text,subs)
                    //Log.e("Debug"," parse_UnorderedListItem $text ")
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
                    //Log.e("Debug"," parse_Qoute $text ")
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
                10->{
                    val reg="^[\\d]*\\.".toRegex().find(string.subSequence(startIndex,endIndex))
                    val order=reg!!.value.subSequence(0,reg!!.value.length-1).toString()
                    text=string.subSequence(startIndex.plus(order.length+1),endIndex)
                    Log.e("Debug","copy_string "+10.toString()+" text="+text+", order="+order)
                    //Log.e("Debug","copy_string "+10.toString()+" text="+text)
                    val element=Element.OrderedListItem(order=order,text=text)
                    parents.add(element)
                    lastStartIndex=endIndex
                }
                11->{
                    text=string.subSequence(startIndex.plus(3),endIndex.minus(3))
                    Log.e("Debug","copy_string "+11.toString()+" text="+text)
                    //val rowsPattern=Pattern.compile("(^.*\\n)|(^.*\$)",Pattern.MULTILINE)
                    //val rowsMatcher = rowsPattern.matcher(text)
                    //rowsMatcher.find()
                    //val rows="(^.*\\n)|(^.*\$)".toRegex().find(text)!!.destructured.toList()
                    val rows="(^.*\$)".toRegex(RegexOption.MULTILINE).findAll(text).map{it.value}.toList()

                    for (index in rows.indices){
                        var bt=Element.BlockCode.Type.SINGLE
                        text=rows[index]
                        if (rows.count()>1){
                        when (index) {
                                0 -> {bt = Element.BlockCode.Type.START
                                    text=text+"\n"}
                                rows.count() - 1 -> bt = Element.BlockCode.Type.END
                                else -> {bt = Element.BlockCode.Type.MIDDLE
                                    text=text+"\n"}
                            }
                        }
                        Log.e("Debug","copy_string "+11.toString()+"circle item="+text+",index="+index.toString()+",type"+bt.toString()+",length="+text.length.toString())
                        val element=Element.BlockCode(text=text,type= bt)
                        parents.add(element)
                    }

                    //val parents= mutableListOf<Element>()



                    //(^.*\n)|(^.*$)
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


    fun clear(string: String): String? {
        var copy_string=string.substring(0,string.length)
        var text=""
        var startIndex=0
        var endIndex=0
        var lastStartIndex=0

        var clrPattern =Pattern.compile(BOLD_GROUP,Pattern.MULTILINE)
        var matcher = clrPattern.matcher(copy_string)
        loop@ while (matcher.find(lastStartIndex)) {
            startIndex = matcher.start()
            endIndex = matcher.end()
            text=copy_string.subSequence(startIndex.plus(2),endIndex.plus(-2)).toString()
            Log.e("Debug","copy_string"+5.toString()+" sb="+copy_string.subSequence(startIndex,endIndex).toString()+" --> "+text)
            //text=clear(text.toString()).toString()
            //copy_string.replace(string.subSequence(startIndex,endIndex).toString(),text.toString(),false)

            //Log.e("Debug","copy_string"+5.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())
            //Log.e("Debug","copy_string"+5.toString()+" copy_string="+copy_string)
            copy_string=copy_string.replaceRange(startIndex,endIndex,"\t\t"+text+"\t\t")
            //Log.e("Debug","copy_string"+5.toString()+" copy_string="+copy_string)
            //Log.e("Debug","copy_string end"+5.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())

            lastStartIndex=endIndex
            //if (lastStartIndex < string.length) {
            //    val text=string.subSequence(lastStartIndex,string.length)
            //}
        }

        clrPattern =Pattern.compile(ITALIC_GROUP,Pattern.MULTILINE)
        matcher = clrPattern.matcher(copy_string)
        lastStartIndex=0
        loop@ while (matcher.find(lastStartIndex)) {
            startIndex = matcher.start()
            endIndex = matcher.end()
            text=copy_string.subSequence(startIndex.inc(),endIndex.dec()).toString()
            Log.e("Debug","copy_string"+4.toString()+" sb="+string.subSequence(startIndex,endIndex).toString()+" --> "+text)
            copy_string=copy_string.replaceRange(startIndex,endIndex,"\t"+text+"\t")
            lastStartIndex=endIndex
        }

        clrPattern =Pattern.compile(UNORDERED_LIST_ITEM_GROUP,Pattern.MULTILINE)
        matcher = clrPattern.matcher(copy_string)
        lastStartIndex=0
        loop@ while (matcher.find(lastStartIndex)) {
            startIndex = matcher.start()
            endIndex = matcher.end()
            text=copy_string.subSequence(startIndex.plus(2),endIndex).toString()
            Log.e("Debug","copy_string"+1.toString()+" sb="+string.subSequence(startIndex,endIndex).toString()+" --> "+text)
            copy_string=copy_string.replaceRange(startIndex,endIndex,"\t"+text+"\t")
            lastStartIndex=endIndex
        }

        clrPattern =Pattern.compile(HEADER_GROUP,Pattern.MULTILINE)
        matcher = clrPattern.matcher(copy_string)
        var i=0
        lastStartIndex=0
        loop@ while (matcher.find(lastStartIndex)) {
            startIndex = matcher.start()
            endIndex = matcher.end()

            val reg="^#{1,6}".toRegex().find(copy_string.subSequence(startIndex,endIndex))
            val level=reg!!.value.length
            text=copy_string.subSequence(startIndex.plus(level.inc()),endIndex).toString()

            i=0
            while (i<level) {text="\t"+text;i++}
            Log.e("Debug","copy_string"+2.toString()+" sb="+string.subSequence(startIndex,endIndex).toString()+" --> "+text)
            copy_string=copy_string.replaceRange(startIndex,endIndex,"\t"+text)
            lastStartIndex=endIndex
        }

        clrPattern =Pattern.compile(QUOTE_GROUP,Pattern.MULTILINE)
        matcher = clrPattern.matcher(copy_string)
        lastStartIndex=0
        loop@ while (matcher.find(lastStartIndex)) {
            startIndex = matcher.start()
            endIndex = matcher.end()
            text=copy_string.subSequence(startIndex.plus(2),endIndex).toString()
            Log.e("Debug","copy_string"+3.toString()+" sb="+string.subSequence(startIndex,endIndex).toString()+" --> "+text)
            copy_string=copy_string.replaceRange(startIndex,endIndex,"\t\t"+text)
            lastStartIndex=endIndex
        }

        clrPattern =Pattern.compile(STRIKE_GROUP,Pattern.MULTILINE)
        matcher = clrPattern.matcher(copy_string)
        lastStartIndex=0
        loop@ while (matcher.find(lastStartIndex)) {
            startIndex = matcher.start()
            endIndex = matcher.end()
            text=copy_string.subSequence(startIndex.plus(2),endIndex.plus(-2)).toString()
            Log.e("Debug","copy_string"+6.toString()+" sb="+copy_string.subSequence(startIndex,endIndex).toString()+" --> "+text)
            //text=clear(text.toString()).toString()
            //copy_string.replace(string.subSequence(startIndex,endIndex).toString(),text.toString(),false)

            //Log.e("Debug","copy_string"+5.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())
            //Log.e("Debug","copy_string"+5.toString()+" copy_string="+copy_string)
            copy_string=copy_string.replaceRange(startIndex,endIndex,"\t\t"+text+"\t\t")
            //Log.e("Debug","copy_string"+5.toString()+" copy_string="+copy_string)
            //Log.e("Debug","copy_string end"+5.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())

            lastStartIndex=endIndex
            //if (lastStartIndex < string.length) {
            //    val text=string.subSequence(lastStartIndex,string.length)
            //}
        }

        clrPattern =Pattern.compile(RULE_GROUP,Pattern.MULTILINE)
        matcher = clrPattern.matcher(copy_string)
        lastStartIndex=0
        loop@ while (matcher.find(lastStartIndex)) {
            startIndex = matcher.start()
            endIndex = matcher.end()
            text=copy_string.subSequence(startIndex,endIndex).toString()
            Log.e("Debug","copy_string"+7.toString()+" sb="+string.subSequence(startIndex,endIndex).toString()+" --> "+text)
            copy_string=copy_string.replaceRange(startIndex,endIndex,"\t\t ")
            lastStartIndex=endIndex
        }

        clrPattern =Pattern.compile(INLINE_GROUP,Pattern.MULTILINE)
        matcher = clrPattern.matcher(copy_string)
        lastStartIndex=0
        loop@ while (matcher.find(lastStartIndex)) {
            startIndex = matcher.start()
            endIndex = matcher.end()
            text=copy_string.subSequence(startIndex.inc(),endIndex.dec()).toString()
            Log.e("Debug","copy_string"+8.toString()+" sb="+string.subSequence(startIndex,endIndex).toString()+" --> "+text)
            copy_string=copy_string.replaceRange(startIndex,endIndex,"\t"+text+"\t")
            lastStartIndex=endIndex
        }

        clrPattern =Pattern.compile(LINK_GROUP,Pattern.MULTILINE)
        matcher = clrPattern.matcher(copy_string)
        lastStartIndex=0
        loop@ while (matcher.find(lastStartIndex)) {
            startIndex = matcher.start()
            endIndex = matcher.end()
            text=copy_string.subSequence(startIndex,endIndex).toString()
            var (title: String, link : String)="\\[(.*)]\\((.*)\\)".toRegex().find(text)!!.destructured
            //title=clear(title.toString()).toString()
            //copy_string=copy_string.replace(string.subSequence(startIndex,endIndex).toString(),title,false)
            i=0;
            text="";
            while (i<link.length+4) {text="\t"+text;i++}
            Log.e("Debug","copy_string"+9.toString()+" sb="+string.subSequence(startIndex,endIndex).toString()+" --> "+text)
            copy_string=copy_string.replaceRange(startIndex,endIndex,text+title)
            lastStartIndex=endIndex
        }

        clrPattern =Pattern.compile(ORDERED_LIST_ITEM_GROUP,Pattern.MULTILINE)
        matcher = clrPattern.matcher(copy_string)
        lastStartIndex=0
        loop@ while (matcher.find(lastStartIndex)) {
            startIndex = matcher.start()
            endIndex = matcher.end()
            val reg="^[\\d]*\\.".toRegex().find(copy_string.subSequence(startIndex,endIndex))
            val order=reg!!.value.subSequence(0,reg!!.value.length-1).toString()
            text='\t'+copy_string.subSequence(startIndex.plus(order.length+1),endIndex).toString()
            //text=copy_string.subSequence(startIndex.plus(3),endIndex.minus(3)).toString()

            i=0
            while (i<order.length) {text="\t"+text;i++}

            Log.e("Debug","copy_string"+10.toString()+" sb="+string.subSequence(startIndex,endIndex).toString()+" --> "+text)
            copy_string=copy_string.replaceRange(startIndex,endIndex,text)
            lastStartIndex=endIndex
        }

        clrPattern =Pattern.compile(MULTILINE_WRAP_BLOCK_CODE_GROUP,Pattern.MULTILINE)
        Log.e("Debug","copy_string"+11.toString()+" matcher="+copy_string)
        matcher = clrPattern.matcher(copy_string)
        lastStartIndex=0
        loop@ while (matcher.find(lastStartIndex)) {
            startIndex = matcher.start()
            endIndex = matcher.end()
            text=copy_string.subSequence(startIndex.plus(3),endIndex.minus(3)).toString()
            Log.e("Debug","copy_string"+11.toString()+" sb="+copy_string.subSequence(startIndex,endIndex).toString()+" --> "+text)
            //copy_string=copy_string.replaceRange(startIndex,endIndex,"\t\t\t"+text+"\t\t\t")
            copy_string=copy_string.replaceRange(startIndex,endIndex,"\t\t\t"+text+"\t\t\t")
            lastStartIndex=endIndex
        }


        copy_string=copy_string.replace("\t","",false)

        Log.e("Debug","copy_string clear="+copy_string)
        return copy_string
    }

    //private fun forLine(block: () -> Unit) {



        fun clear1(string: String): String?{
        var copy_string=string.substring(0,string.length)
        copy_string=copy_string.replace("###### H","H",false)
        copy_string=copy_string.replace("##### H","H",false)
        copy_string=copy_string.replace("#### H","H",false)
        copy_string=copy_string.replace("### H","H",false)
        copy_string=copy_string.replace("## H","H",false)
        copy_string=copy_string.replace("# H","H",false)
        copy_string=copy_string.replace("# ","",false)
        copy_string=copy_string.replace("*","",false)
        copy_string=copy_string.replace("_","",false)
        copy_string=copy_string.replace("~","",false)
        copy_string=copy_string.replace(">","",false)
        copy_string=copy_string.replace("_","",false)
        copy_string=copy_string.replace("+","",false)
        copy_string=copy_string.replace("---","",false)
        copy_string=copy_string.replace("-","",false)
        copy_string=copy_string.replace("`(","(",false)
        copy_string=copy_string.replace(")`",")",false)
        copy_string=copy_string.replace("]`","]",false)
        copy_string=copy_string.replace("`[","[",false)
        copy_string=copy_string.replace("` "," ",false)
        copy_string=copy_string.replace(" `"," ",false)


        //Log.e("Debug","copy_string first fun string="+string)
        val matcher = elementsPatten.matcher(copy_string)

        //var copy_string=string.substring(0,string.length)



        //Log.e("Debug","copy_string start="+copy_string)
        //"JGKJHlksjdlsjdgklsjglksjglksjglksjglksgjklsgjklsg"//string
        var lastStartIndex=0
        //string?.replace("","",false)
        //Нужно найти все элементы и выделить из них текст
        //и сложить
        // то есть переходим по группам
        //находим совпадения
        //заменяем найденное на простой текст
        //идем дальше

        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex=matcher.start()
            val endIndex=matcher.end()

            Log.e("Debug","copy_string while lastStartIndex="+lastStartIndex.toString()+
                    ",startIndex="+startIndex.toString()+",endIndex="+endIndex.toString())


            //if(lastStartIndex < startIndex){
            //    parents.add(Element.Text(string.subSequence(lastStartIndex,startIndex)))
            //}

            var text: CharSequence

            val groups=1..9
            var group=-1
            for(gr in groups){
                if(matcher.group(gr)!=null){
                    group=gr
                    break
                }
            }
            Log.e("Debug","copy_string group="+group.toString())

            //Log.e("Debug"," parse_group $group ")
            when(group) {
                -1 -> break@loop
                10->{
                    text=copy_string.subSequence(startIndex.plus(2),endIndex)
                    text=clear(text.toString()).toString()
                    copy_string=copy_string.replace(copy_string.subSequence(startIndex,endIndex).toString(),text.toString(),false)
                    //Log.e("Debug","copy_string"+group.toString()+"="+copy_string)
                    //Log.e("Debug","copy_string"+group.toString()+" text="+text)

                    //text=clear(text.toString()).toString()
                    //Log.e("Debug","copy_string"+group.toString()+" after clear text="+text)
                    //Log.e("Debug","clear copy_string="+clear(copy_string).toString())
                    //copy_string=clear(copy_string).toString()

                    //val subs= findElements(text)
                    //val element=Element.UnorderedListItem(text,subs)
                    //Log.e("Debug"," parse_UnorderedListItem $text ")
                    //parents.add(element)

                    lastStartIndex=endIndex
                }
                20->{
                    val reg="^#{1,6}".toRegex().find(copy_string.subSequence(startIndex,endIndex))
                    val level=reg!!.value.length
                    text=copy_string.subSequence(startIndex.plus(level.inc()),endIndex)
                    text=clear(text.toString()).toString()
                    //Log.e("Debug","clear_header text"+group.toString()+"="+text)
                    //Log.e("Debug","clear_header subsequence"+group.toString()+"="+string.subSequence(startIndex,endIndex))
                    copy_string=copy_string.replace(copy_string.subSequence(startIndex,endIndex).toString(),text.toString(),false)
                    //Log.e("Debug","clear_header copy_string "+group.toString()+"="+copy_string)
                    //Log.e("Debug","copy_string"+group.toString()+"="+copy_string)
                    //Log.e("Debug","copy_string"+group.toString()+" text="+text)
                    //Log.e("Debug","copy_string"+group.toString()+" after clear text="+text)
                    //copy_string=clear(copy_string).toString()

                    //val element=Element.Header(level,text)
                    //Log.e("Debug"," parse_Qoute $text ")
                    //parents.add(element)
                    lastStartIndex=endIndex
                }
                30->{
                    //val reg="^>".toRegex().find(string.subSequence(startIndex,endIndex))
                    //val level=reg!!.value.length
                    text=string.subSequence(startIndex.plus(2),endIndex)
                    //Log.e("Debug","copy_string"+group.toString()+" subSequence="+string.subSequence(startIndex,endIndex))
                    //Log.e("Debug","copy_string"+group.toString()+" text="+text)
                    text=clear(text.toString()).toString()
                    //Log.e("Debug","copy_string"+group.toString()+" next text="+text)
                    //Log.e("Debug","copy_string"+group.toString()+"="+copy_string)
                    //Log.e("Debug","copy_string"+group.toString()+" text="+text)
                    //text=clear(text.toString()).toString()
                    copy_string=copy_string.replace(string.subSequence(startIndex,endIndex).toString(),text.toString(),false)
                    //text=clear(text.toString()).toString()
                    //Log.e("Debug","copy_string"+group.toString()+" after clear text="+text)


                    //val subelements=findElements(text)

                    //val element=Element.Quote(text,subelements)
                    //Log.e("Debug"," parse_Qoute $text ")
                    //parents.add(element)
                    lastStartIndex=endIndex
                }
                40->{
                    text=string.subSequence(startIndex.inc(),endIndex.dec())
                    //Log.e("Debug","copy_string"+group.toString()+" subSequence="+string.subSequence(startIndex,endIndex))
                    //Log.e("Debug","copy_string"+group.toString()+" text="+text)
                    //text=clear(text.toString()).toString()
                    //Log.e("Debug","copy_string"+group.toString()+" next text="+text)
                    copy_string=copy_string.replace(string.subSequence(startIndex,endIndex).toString(),text.toString(),false)
                    //Log.e("Debug","copy_string"+group.toString()+" text="+text)
                    //Log.e("Debug","copy_string"+group.toString()+" after clear text="+text)
                    //Log.e("Debug","copy_string"+group.toString()+"="+copy_string)


                    //val subelements=findElements(text)

                    //val element=Element.Italic(text,subelements)
                    //parents.add(element)


                    lastStartIndex=endIndex
                }
                50->{
                    Log.e("Debug","copy_string"+group.toString()+" subSequence="+string.subSequence(startIndex,endIndex))
                    text=string.subSequence(startIndex.plus(2),endIndex.plus(-2))
                    Log.e("Debug","copy_string"+group.toString()+" text="+text)
                    text=clear(text.toString()).toString()
                    Log.e("Debug","copy_string"+group.toString()+"  2 text="+text)
                    copy_string.replace(string.subSequence(startIndex,endIndex).toString(),text.toString(),false)
                    //Log.e("Debug","copy_string"+group.toString()+" text="+text)
                    //text=clear(text.toString()).toString()
                    //Log.e("Debug","copy_string"+group.toString()+" after clear text="+text)
                    //Log.e("Debug","copy_string"+group.toString()+"="+copy_string)


                    //val subelements=findElements(text)

                    //val element=Element.Bold(text,subelements)
                    //parents.add(element)


                    lastStartIndex=endIndex
                }
                60->{
                    text=string.subSequence(startIndex.plus(2),endIndex.plus(-2))
                    text=clear(text.toString()).toString()
                    copy_string=copy_string.replace(string.subSequence(startIndex,endIndex).toString(),text.toString(),false)
                    //Log.e("Debug","copy_string"+group.toString()+" text="+text)
                    //text=clear(text.toString()).toString()
                    //Log.e("Debug","copy_string"+group.toString()+" after clear text="+text)
                    ////Log.e("Debug","copy_string"+group.toString()+"="+copy_string)

                    //val subelements=findElements(text)

                    //val element=Element.Strike(text,subelements)
                    //parents.add(element)


                    lastStartIndex=endIndex
                }
                70->{
                    //val element=Element.Rule()
                    //parents.add(element)
                    lastStartIndex=endIndex
                }
                80->{
                    text=copy_string.subSequence(startIndex.inc(),endIndex.dec())
                    //text=clear(text.toString()).toString()
                    copy_string=copy_string.replace(copy_string.subSequence(startIndex,endIndex).toString(),text.toString(),false)

                    //Log.e("Debug","copy_string"+group.toString()+" text="+text)
                    //text=clear(text.toString()).toString()
                    //Log.e("Debug","copy_string"+group.toString()+" after clear text="+text)
                    //Log.e("Debug","copy_string"+group.toString()+"="+copy_string)


                    //val element=Element.InlineCode(text)
                    //parents.add(element)

                    lastStartIndex=endIndex

                }
                9->{
                    text=copy_string.subSequence(startIndex,endIndex)
                    Log.e("Debug","copy_string"+group.toString()+" text="+text)
                    var (title: String, link : String)="\\[(.*)]\\((.*)\\)".toRegex().find(text)!!.destructured
                    Log.e("Debug","copy_string"+group.toString()+" title="+title)
                    //title=clear(title.toString()).toString()
                    copy_string=copy_string.replace(copy_string.subSequence(startIndex,endIndex).toString(),title,false)
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)

                    //text1=text.toString().substring(0,text.length)
                    //text=clear(text.toString()).toString()
                    //Log.e("Debug","copy_string"+group.toString()+" after clear text="+text)
                    //Log.e("Debug","copy_string"+group.toString()+"="+copy_string)

                    //val (title: String, link : String)="\\[(.*)]\\((.*)\\)".toRegex().find(text)!!.destructured
                    //val element=Element.Link(text=title,link=link)
                    //parents.add(element)

                    lastStartIndex=endIndex

                }
            }
            //if (text.equals(text1)==true) break

        }

        if (lastStartIndex < copy_string.length) {
            val text=copy_string.subSequence(lastStartIndex,copy_string.length)
            //text=clear(text.toString()).toString()
            //copy_string.replace(string.subSequence(startIndex,endIndex).toString(),text.toString(),false)

            //parents.add(Element.Text(text))
        }

        return copy_string
    }



    fun clear2(string: String): String?{
        var copy_string=string.substring(0,string.length)
        val matcher = elementsPatten.matcher(string)
        var lastStartIndex=0
        var i=0

        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex=matcher.start()
            val endIndex=matcher.end()

            var text: CharSequence

            val groups=1..9
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
                    //text=clear(text.toString()).toString()
                    Log.e("Debug","copy_string"+group.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)
                    copy_string=copy_string.replaceRange(startIndex,endIndex,"\t\t"+text)
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)
                    Log.e("Debug","copy_string end"+group.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())

                    //replace(string.subSequence(startIndex,endIndex).toString(),text.toString(),false)

                    lastStartIndex=endIndex
                }
                2->{

                    val reg="^#{1,6}".toRegex().find(string.subSequence(startIndex,endIndex))
                    val level=reg!!.value.length
                    text=string.subSequence(startIndex.plus(level.inc()),endIndex)
                    i=0;
                    while (i<level) {text="\t"+text;i++}
                    Log.e("Debug","copy_string"+group.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)
                    copy_string=copy_string.replaceRange(startIndex,endIndex,"\t"+text)
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)
                    Log.e("Debug","copy_string end"+group.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())
                    //text=clear(text.toString()).toString()
                    //copy_string=copy_string.replace(string.subSequence(startIndex,endIndex).toString(),text.toString(),false)

                    lastStartIndex=endIndex
                }
                3->{

                    text=string.subSequence(startIndex.plus(2),endIndex)
                    //text=clear(text.toString()).toString()
                    //copy_string=copy_string.replace(string.subSequence(startIndex,endIndex).toString(),text.toString(),false)

                    Log.e("Debug","copy_string"+group.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)
                    copy_string=copy_string.replaceRange(startIndex,endIndex,"\t\t"+text)
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)
                    Log.e("Debug","copy_string end"+group.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())
                    lastStartIndex=endIndex
                }
                4->{

                    text=string.subSequence(startIndex.inc(),endIndex.dec())
                    //text=clear(text.toString()).toString()
                    //copy_string=copy_string.replace(string.subSequence(startIndex,endIndex).toString(),text.toString(),false)
                    Log.e("Debug","copy_string"+group.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)
                    copy_string=copy_string.replaceRange(startIndex,endIndex,"\t"+text+"\t")
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)
                    Log.e("Debug","copy_string end"+group.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())


                    lastStartIndex=endIndex
                }
                5->{

                    text=string.subSequence(startIndex.plus(2),endIndex.plus(-2))
                    //text=clear(text.toString()).toString()
                    //copy_string.replace(string.subSequence(startIndex,endIndex).toString(),text.toString(),false)

                    Log.e("Debug","copy_string"+group.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)
                    copy_string=copy_string.replaceRange(startIndex,endIndex,"\t\t"+text+"\t\t")
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)
                    Log.e("Debug","copy_string end"+group.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())


                    lastStartIndex=endIndex
                }
                6->{

                    text=string.subSequence(startIndex.plus(2),endIndex.plus(-2))
                    //text=clear(text.toString()).toString()
                    //copy_string=copy_string.replace(string.subSequence(startIndex,endIndex).toString(),text.toString(),false)

                    Log.e("Debug","copy_string"+group.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)
                    copy_string=copy_string.replaceRange(startIndex,endIndex,"\t\t"+text+"\t\t")
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)
                    Log.e("Debug","copy_string end"+group.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())


                    lastStartIndex=endIndex
                }
                7->{
                    /*
                    //val element=Element.Rule()
                    //parents.add(element)
                    */
                    lastStartIndex=endIndex
                }
                8->{

                    text=string.subSequence(startIndex.inc(),endIndex.dec())
                    //text=clear(text.toString()).toString()
                    //copy_string=copy_string.replace(string.subSequence(startIndex,endIndex).toString(),text.toString(),false)

                    Log.e("Debug","copy_string"+group.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)
                    copy_string=copy_string.replaceRange(startIndex,endIndex,"\t"+text+"\t")
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)
                    Log.e("Debug","copy_string end"+group.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())


                    lastStartIndex=endIndex
                }
                9->{

                    text=string.subSequence(startIndex,endIndex)
                    var (title: String, link : String)="\\[(.*)]\\((.*)\\)".toRegex().find(text)!!.destructured
                    //title=clear(title.toString()).toString()
                    //copy_string=copy_string.replace(string.subSequence(startIndex,endIndex).toString(),title,false)
                    i=0;
                    text="";
                    while (i<link.length+4) {text="\t"+text;i++}

                    Log.e("Debug","copy_string"+group.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)

                    copy_string=copy_string.replaceRange(startIndex,endIndex,text.toString()+title)
                    Log.e("Debug","copy_string"+group.toString()+" copy_string="+copy_string)
                    Log.e("Debug","copy_string end"+group.toString()+" text="+text+",startindex="+startIndex.toString()+",endindex="+endIndex.toString()+",length="+copy_string.length.toString())

                    lastStartIndex=endIndex
                }
            }

        }

        if (lastStartIndex < string.length) {
            val text=string.subSequence(lastStartIndex,string.length)
        }
        copy_string=copy_string.replace("\t","",false)
        Log.e("Debug","copy_string the end of fun"+" copy_string="+copy_string)
        return copy_string
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

    data class OrderedListItem(
        val order: String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class BlockCode(
        val type: Type = Type.MIDDLE,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element() {
        enum class Type { START, END, MIDDLE, SINGLE }
    }


}