package ru.skillbranch.skillarticles.markdown

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.SpannedString
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToPx
import ru.skillbranch.skillarticles.markdown.spans.UnorderedListSpan
import  ru.skillbranch.skillarticles.ui.delegates.AttrValue

class MarkdownBuilder(context: Context) {

    private val gap: Float=context.dpToPx(8)
    private val bulletRadius=context.dpToPx(4)
    private val colorSecondary=context.attrValue(R.attr.colorSecondary)
    //private val colorSecondary=R.attr.colorSecondary

    fun markdownToSpan(string: String): SpannedString {
        val markdown=MarkdownParser.parse(string)
        return buildSpannedString{
            markdown.elements.forEach{buildElement(it,this)}
        }
        }

        private fun buildElement(element: Element,builder: SpannableStringBuilder):CharSequence{
                return builder.apply {
                    when(element){
                        is Element.Text->append(element.text)
                        is Element.UnorderedListItem->{
                            inSpans(UnorderedListSpan(gap,bulletRadius,colorSecondary)){
                                for (child in element.elements){
                                    buildElement(child,builder)
                                }
                            }
                        }
                    }
                }
        }
}
