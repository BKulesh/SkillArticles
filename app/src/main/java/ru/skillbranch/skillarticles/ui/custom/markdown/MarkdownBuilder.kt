package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.SpannedString
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.repositories.Element
import ru.skillbranch.skillarticles.data.repositories.MarkdownParser
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToPx
import ru.skillbranch.skillarticles.ui.custom.spans.*

class MarkdownBuilder(context: Context) {

    private val gap: Float=context.dpToPx(8)
    private val bulletRadius=context.dpToPx(4)
    private val strikeWidth=context.dpToPx(4)
    private val quoteWidth=context.dpToPx(4)
    private val colorSecondary=context.attrValue(R.attr.colorSecondary)
    private val colorPrimary=context.attrValue(R.attr.colorPrimary)
    private val colorDevider=context.getColor(R.color.color_divider)
    private val headerMarginTop=context.dpToPx(12)
    private val haderMrginBottom=context.dpToPx(8)
    private val ruleWidth=context.dpToPx(2)

    private val colorOnSurface=context.attrValue(R.attr.colorOnSurface)
    private val colorSurface=context.attrValue(R.attr.colorSurface)
    private val cornerRadius=context.dpToPx(8)
    //private val colorSecondary=R.attr.colorSecondary
    private val linkIcon=context.getDrawable(R.drawable.ic_link_black_24dp)!!.apply{
        setTint(colorSecondary)
    }


    fun markdownToSpan(string: String): SpannedString {
        val markdown=
            MarkdownParser.parse(string)
        //Log.e("Debug","Builder Action buildElement BEFORE")
        return buildSpannedString{
            markdown.elements.forEach{buildElement(it,this)
            //Log.e("Debug","Builder Action buildElement="+it.text)
            }
        }
        }

        private fun buildElement(element: Element, builder: SpannableStringBuilder):CharSequence{
                return builder.apply {
                    //Log.e("Debug","buildElement apply")
                    when(element){
                        is Element.Text->{append(element.text)
                            //Log.e("Debug","append element text")
                             }
                        is Element.UnorderedListItem->{
                            //Log.e("Debug","append element UnorderedListItem ${element.text}")
                            inSpans(UnorderedListSpan(gap,bulletRadius,colorSecondary)){
                                //Log.e("Debug","inSpans action ha")
                                for (child in element.elements){
                                    buildElement(child,builder)
                                }
                            }
                        }
                        is Element.Quote->{
                            inSpans(BlockquotesSpan(gap,quoteWidth ,colorSecondary),
                                    StyleSpan(Typeface.ITALIC)
                            )
                            {
                                for (child in element.elements){
                                    buildElement(child,builder)
                                }
                            }
                        }
                        is Element.Header->{
                            inSpans(HeaderSpan(element.level,colorPrimary,colorDevider,headerMarginTop,haderMrginBottom)){
                                append(element.text)
                            }
                        }
                        is Element.Italic-> { inSpans(StyleSpan(Typeface.ITALIC))                            {
                            for (child in element.elements){
                                buildElement(child,builder)
                            }
                        }
                        }
                        is Element.Bold-> { inSpans(StyleSpan(Typeface.BOLD))                            {
                            for (child in element.elements){
                                buildElement(child,builder)
                            }
                        }
                        }
                        is Element.Strike-> { inSpans(StrikethroughSpan())                            {
                            for (child in element.elements){
                                buildElement(child,builder)
                            }
                        }
                        }
                        is Element.Rule->{inSpans(HorizontalRuleSpan(ruleWidth,colorDevider)){
                            append(element.text)
                        }
                        }
                        is Element.InlineCode->{
                            inSpans(InlineCodeSpan(colorOnSurface,colorSurface,cornerRadius,gap)){
                                append(element.text)
                            }
                        }
                        is Element.Link->{
                            inSpans(IconLinkSpan(linkIcon,gap,colorPrimary,strikeWidth),
                                    URLSpan(element.link)
                            ){
                                append(element.text)
                            }
                        }
                        is Element.BlockCode->{
                            inSpans(BlockCodeSpan(colorOnSurface,colorSurface,cornerRadius,gap,element.type)
                            ){
                              append(element.text)
                            }
                        }
                        is Element.OrderedListItem->{
                            inSpans(OrderedListSpan(gap,element.order,colorOnSurface)){
                                append(element.text)
                            }
                        }
                        else  -> {append(element.text)
                                  //Log.e("Debug","else append")
                            }
                    }
                }
        }
}