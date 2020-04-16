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

class SearchBgHelper (
    context: Context,
    private val focusListener: (Int) -> Unit
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
            MultiLineRender(padding,drawble)
    }


    private lateinit var spans: Array<out SearchSpan>

    private var spanStart=0
    private var spanEnd=0
    private var startLine=0
    private var endLine=0
    private var startOffSet=0
    private var endOffSet=0

    fun Draw(canvas: Canvas, text: Spanned, layout: Layout){
        spans=text.getSpans()
        spans.forEach{
            spanStart=text.getSpanStart(it)
            spanEnd=text.getSpanEnd(it)
            startLine=layout.getLineForOffset(spanStart)
            endLine=layout.getLineForOffset(spanEnd)

            startOffSet=layout.getPrimaryHorizontal(spanStart).toInt()
            endOffSet=layout.getPrimaryHorizontal(spanEnd).toInt()

            render= if (startLine==endLine) singleLineRender else multiLineRender
            render.draw()

        }
    }

}

abstract class searchBgRender(val padding: Int){
    abstract fun Draw (
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
        return layout.getLineTop(line)
    }

    fun getLineBottom(layout: Layout,line: Int): Int{
        return layout.getLineBottom(line)
    }
}

class SingleLineRender(
    padding: Int,
    val drawble: Drawable
): searchBgRender(padding){
    private var lineTop: Int=0
    private var lineBottom: Int=0

    override fun Draw (
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffSet: Int,
        topExtraPadding: Int,
        bottomExtraPadding: Int
    ) {
        lineTop=getLineTop(layout,startLine)
        lineBottom=getLineBottom(layout,startLine)
        drawble.setBounds(startOffset,lineTop,endOffSet,lineBottom)
        drawble.draw(canvas)
    }

}

class MultiLineRender(
    padding: Int,
    val drawble: Drawable
): searchBgRender(padding){
    private var lineTop: Int=0
    private var lineBottom: Int=0

    override fun Draw (
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffSet: Int,
        topExtraPadding: Int,
        bottomExtraPadding: Int
    ) {
        lineTop=getLineTop(layout,startLine)
        lineBottom=getLineBottom(layout,startLine)
        drawble.setBounds(startOffset,lineTop,endOffSet,lineBottom)
        drawble.draw(canvas)
    }

}