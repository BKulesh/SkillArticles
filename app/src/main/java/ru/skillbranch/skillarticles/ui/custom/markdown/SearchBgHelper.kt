package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.Layout
import android.text.Spanned
import androidx.core.graphics.ColorUtils
import androidx.core.text.getSpans
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.ui.custom.spans.SearchSpan
import ru.skillbranch.skillarticles.extensions.*
import ru.skillbranch.skillarticles.ui.custom.spans.HeaderSpan
import ru.skillbranch.skillarticles.ui.custom.spans.SearchFocusSpan

class SearchBgHelper (
    context: Context,
    private val focusListener: (Int,Int) -> Unit
) {
  private val padding: Int=context.dpToIntPx(4)
  private val radius: Float=context.dpToIntPx(8).toFloat()
  private val borderWidth: Int=context.dpToIntPx(1)

  private val secondaryColor: Int=context.attrValue(ru.skillbranch.skillarticles.R.attr.colorSecondary)
  private val alphaColor: Int=ColorUtils.setAlphaComponent(secondaryColor,160)

    val drawble: Drawable by lazy{
        GradientDrawable().apply {
            shape=GradientDrawable.RECTANGLE
            //cornerRadii=FloatArray(8).apply { fill(radius,0,size) }
            color= ColorStateList.valueOf(alphaColor)
            setStroke(borderWidth,secondaryColor)
        }

    }

  val drawbleMiddle: Drawable by lazy{
      GradientDrawable().apply {
          shape=GradientDrawable.RECTANGLE
          //cornerRadii=FloatArray(8).apply { fill(radius,0,size) }
          color= ColorStateList.valueOf(alphaColor)
          setStroke(borderWidth,secondaryColor)
      }

  }

    val drawbleLeft: Drawable by lazy{
        GradientDrawable().apply {
            shape=GradientDrawable.RECTANGLE
            cornerRadii=floatArrayOf(radius,radius,0f,0f,0f,0f,radius,radius)
            color= ColorStateList.valueOf(alphaColor)
            setStroke(borderWidth,secondaryColor)
        }

    }

    val drawbleRight: Drawable by lazy{
        GradientDrawable().apply {
            shape=GradientDrawable.RECTANGLE
            cornerRadii=floatArrayOf(0f,0f,radius,radius,radius,radius,0f,0f)
            color= ColorStateList.valueOf(alphaColor)
            setStroke(borderWidth,secondaryColor)
        }

    }


    private lateinit var render:searchBgRender
    private val singleLineRender:searchBgRender by lazy {
        SingleLineRender(padding,drawble)
    }

    private val multiLineRender:searchBgRender by lazy {
            MultiLineRender(padding,
                drawbleLeft,
                drawbleMiddle,
                drawbleRight)
    }


    private lateinit var spans: Array<out SearchSpan>
    private lateinit var headerSpans: Array<out HeaderSpan>

    private var spanStart=0
    private var spanEnd=0
    private var startLine=0
    private var endLine=0
    private var startOffSet=0
    private var endOffSet=0
    private var topExtraPadding=0
    private var bottomExtraPadding=0

    fun draw(canvas: Canvas, text: Spanned, layout: Layout){
        spans=text.getSpans()
        spans.forEach{
            spanStart=text.getSpanStart(it)
            spanEnd=text.getSpanEnd(it)
            startLine=layout.getLineForOffset(spanStart)
            endLine=layout.getLineForOffset(spanEnd)

            if (it is SearchFocusSpan) {
                focusListener.invoke(layout.getLineTop(startLine),layout.getLineBottom(startLine))
            }

            headerSpans=text.getSpans(spanStart,spanEnd, HeaderSpan::class.java)

            topExtraPadding=0
            bottomExtraPadding=0

            if (headerSpans.isNotEmpty()){
                    topExtraPadding=if (spanStart in headerSpans[0].firstLineBounds || spanEnd in headerSpans[0].firstLineBounds) headerSpans[0].topExtraPadding else 0
                    bottomExtraPadding=if (spanStart in headerSpans[0].lastLineBounds || spanEnd in headerSpans[0].lastLineBounds) headerSpans[0].bottomExtraPadding else 0
                }

            startOffSet=layout.getPrimaryHorizontal(spanStart).toInt()
            endOffSet=layout.getPrimaryHorizontal(spanEnd).toInt()

            render= if (startLine==endLine) singleLineRender else multiLineRender
            render.draw(canvas,layout,startLine,endLine,startOffSet,endOffSet,topExtraPadding,bottomExtraPadding)
        }
    }

}

abstract class searchBgRender(val padding: Int){
    abstract fun draw (
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffSet: Int,
        topExtraPadding: Int=0,
        bottomExtraPadding: Int=0
    )

    fun getLineTop(layout: Layout,line: Int): Int{
        return layout.getLineTopWithoutPadding(line)
    }

    fun getLineBottom(layout: Layout,line: Int): Int{
        return layout.getLineBottomWithoutPadding(line)
    }
}

class SingleLineRender(
    padding: Int,
    val drawble: Drawable
): searchBgRender(padding){
    private var lineTop: Int=0
    private var lineBottom: Int=0

    override fun draw (
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffSet: Int,
        topExtraPadding: Int,
        bottomExtraPadding: Int
    ) {
        lineTop=getLineTop(layout,startLine)+topExtraPadding
        lineBottom=getLineBottom(layout,startLine)-bottomExtraPadding
        drawble.setBounds(startOffset-padding,lineTop,endOffSet+padding,lineBottom)
        drawble.draw(canvas)
    }

}

class MultiLineRender(
    padding: Int,
    val drawbleLeft: Drawable,
    val drawbleMiddle: Drawable,
    val drawbleRight: Drawable
): searchBgRender(padding){
    private var lineTop: Int=0
    private var lineBottom: Int=0
    private var lineEndOffset=0
    private var lineStartOffset=0

    override fun draw (
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffSet: Int,
        topExtraPadding: Int,
        bottomExtraPadding: Int
    ) {
        lineEndOffset=(layout.getLineRight(startLine)+padding).toInt()
        lineTop=getLineTop(layout,startLine)
        lineBottom=getLineBottom(layout,startLine)
        drawStart(canvas,startOffset-padding,lineTop,lineEndOffset,lineBottom)

        for (line in startLine.inc() until endLine) {
            lineTop=getLineTop(layout,line)+topExtraPadding
            lineBottom=getLineTop(layout,line)
            drawbleMiddle.setBounds(layout.getLineLeft(line).toInt()-padding,lineTop,
            layout.getLineRight(line).toInt()+padding,lineBottom)
        }

        lineStartOffset=(layout.getLineLeft(startLine)-padding).toInt()
        lineTop=getLineTop(layout,endLine)-bottomExtraPadding
        lineBottom=getLineBottom(layout,endLine)
        drawEnd(canvas,lineStartOffset,lineTop,endOffSet+padding,lineBottom)

    }

    private fun drawStart(canvas: Canvas,start: Int, top: Int,end: Int,bottom: Int) {
        drawbleLeft.setBounds(start,top,end,bottom)
        drawbleLeft.draw(canvas)
    }

    private fun drawEnd(canvas: Canvas,start: Int, top: Int,end: Int,bottom: Int) {
        drawbleRight.setBounds(start,top,end,bottom)
        drawbleRight.draw(canvas)
    }

}