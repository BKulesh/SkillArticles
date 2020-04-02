package ru.skillbranch.skillarticles

import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.text.*
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.text.getSpans
import androidx.core.view.marginBottom
import androidx.core.widget.NestedScrollView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.ArgumentMatchers
import org.mockito.InOrder
import org.mockito.Mockito.*
import ru.skillbranch.skillarticles.data.LocalDataHolder
import ru.skillbranch.skillarticles.data.NetworkDataHolder
import ru.skillbranch.skillarticles.extensions.indexesOf
import ru.skillbranch.skillarticles.extensions.setMarginOptionally
import ru.skillbranch.skillarticles.markdown.Element
import ru.skillbranch.skillarticles.markdown.spans.*
import ru.skillbranch.skillarticles.ui.RootActivity
import ru.skillbranch.skillarticles.ui.custom.SearchFocusSpan
import ru.skillbranch.skillarticles.ui.custom.SearchSpan
import ru.skillbranch.skillarticles.viewmodels.ArticleState
import java.lang.Thread.sleep


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest1 {

    @Test
    fun draw_list_item(){
        val color= Color.RED
        val gap=24f
        val radius=8f

        val canvasWidth=700
        val defaultColor=Color.GRAY

        val canvas=mock(Canvas::class.java)
        val paint=mock(Paint::class.java)
        `when`(paint.color).thenReturn(defaultColor)
        val layout=mock(Layout::class.java)

        val cml=0
        val ltop=0
        val lbase=60
        val lbottom=80

        val text= SpannableString("text")
        val span=UnorderedListSpan(gap,radius,color)
        text.setSpan(span,0,text.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        assertEquals((4*radius+gap).toInt(),span.getLeadingMargin(true))

        span.drawLeadingMargin(canvas,paint,cml,1,ltop,lbase,lbottom,text,0,text.length,true ,layout)

        val inOrder=inOrder(paint,canvas)
        inOrder.verify(paint).color=color
        inOrder.verify(canvas).drawCircle(gap+cml+radius,(lbottom-ltop)/2f+ltop,radius,paint)
        inOrder.verify(paint).color=defaultColor

    }

    @Test
    fun draw_qoute(){
        val color= Color.RED
        val gap=24f
        val lineWidth=8f

        val canvasWidth=700
        val lineColor=Color.GRAY

        val canvas=mock(Canvas::class.java)
        val paint=mock(Paint::class.java)
        `when`(paint.color).thenReturn(lineColor)
        val layout=mock(Layout::class.java)

        val cml=0
        val ltop=0
        val lbase=60
        val lbottom=80

        val text= SpannableString("text")

        val span=BlockquotesSpan(gap,lineWidth,color)
        text.setSpan(span,0,text.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        assertEquals((lineWidth+gap).toInt(),span.getLeadingMargin(true))

        span.drawLeadingMargin(canvas,paint,cml,1,ltop,lbase,lbottom,text,0,text.length,true ,layout)

        val inOrder=inOrder(paint,canvas)
        inOrder.verify(paint).color=color
        inOrder.verify(paint).strokeWidth=lineWidth
        inOrder.verify(canvas).drawLine(lineWidth/2,ltop.toFloat(),lineWidth/2,lbottom.toFloat(),paint)
        inOrder.verify(paint).color=lineColor

    }


    @Test
    fun draw_header(){
        val level=1
        val defaultColor= Color.GRAY
        val textColor= Color.RED
        val lineColor= Color.RED
        val marginTop=24f
        val marginBottom=8f

        val canvasWidth=700

        val canvas=mock(Canvas::class.java)
        `when`(canvas.width).thenReturn(canvasWidth)
        val paint=mock(Paint::class.java)
        `when`(paint.color).thenReturn(lineColor)
        val measurePaint=mock(TextPaint::class.java)
        val drawPaint=mock(TextPaint::class.java)
        val layout=mock(Layout::class.java)

        val cml=0
        val ltop=0
        val lbase=60
        val lbottom=80

        val text= SpannableString("text")

        val span= HeadersSpan(level,textColor,lineColor,marginTop,marginBottom)
        text.setSpan(span,0,text.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        assertEquals(0,span.getLeadingMargin(true))

        span.updateMeasureState(measurePaint)
        verify(measurePaint).textSize*=span.sizes[level]!!
        verify(measurePaint).isFakeBoldText=true

        span.updateDrawState(drawPaint)
        verify(drawPaint).textSize*=span.sizes[level]!!
        verify(drawPaint).isFakeBoldText=true
        verify(drawPaint).color=textColor

        span.drawLeadingMargin(canvas,paint,cml,1,ltop,lbase,lbottom,text,0,text.length,true ,layout)

        val inOrder=inOrder(paint,canvas)
        inOrder.verify(paint).color = lineColor
        val lh=(paint.descent()-paint.ascent())*span.sizes[level]!!
        val lineOffset=lbase-lh*span.linePadding

        inOrder.verify(canvas).drawLine(0f,lineOffset,canvasWidth.toFloat(),lineOffset,paint)

        inOrder.verify(paint).color=defaultColor


    }

    @Test
    fun draw_rule(){
        val color= Color.RED
        val width= 24f

        val canvasWidth=700
        val defaultColor= Color.GRAY
        val cml=0
        val ltop=0
        val lbase=60
        val lbottom=80

        val canvas=mock(Canvas::class.java)
        `when`(canvas.width).thenReturn(canvasWidth)
        val paint=mock(Paint::class.java)
        `when`(paint.color).thenReturn(defaultColor)
        val layout=mock(Layout::class.java)

        val text= SpannableString("text")

        val span= HorizontalRuleSpan(width,color)
        text.setSpan(span,0,text.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        span.draw(canvas,text,0,text.length,cml.toFloat(),ltop,lbase,lbottom,paint)

        val InOrder=inOrder(paint,canvas)

        InOrder.verify(paint).color=color

        InOrder.verify(canvas).drawLine(0f,(ltop+lbottom)/2f,canvasWidth.toFloat(),(ltop-lbottom)/2f,paint)

        InOrder.verify(paint).color=defaultColor


    }

    @Test
    fun draw_inline_code(){

        val textColor: Int=Color.RED
        val bgColor: Int=Color.GREEN
        val cornerRadius: Float =8f
        val padding: Float =8f

        val canvasWidth=700
        val defaultColor=Color.GRAY
        val measureText=100f
        val cml=0
        val ltop=0
        val lbase=60
        val lbottom=80

        val canvas=mock(Canvas::class.java)
        `when`(canvas.width).thenReturn(canvasWidth)
        val paint=mock(Paint::class.java)
        `when`(paint.color).thenReturn(defaultColor)
        `when`(paint.measureText(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyInt(),
            ArgumentMatchers.anyInt()
        )).thenReturn(measureText)
        val layout=mock(Layout::class.java)
        val fm=mock(Paint.FontMetricsInt::class.java)

        val text= SpannableString("text")

        val span= InlineCodeSpan(textColor,bgColor,cornerRadius,padding)
        text.setSpan(span,0,text.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val size=span.getSize(paint,text,0,text.length,fm)
        assertEquals((2*padding+measureText).toInt(),size)

        val inOrder=inOrder(paint,canvas)

        inOrder.verify(paint).color=bgColor
        inOrder.verify(canvas).drawRoundRect(RectF(0f,ltop.toFloat(),measureText+2*padding,lbottom.toFloat()),cornerRadius,cornerRadius,paint)

        inOrder.verify(paint).color=textColor
        inOrder.verify(canvas).drawText(text,0,text.length,cml+padding,lbase.toFloat(),paint)
        inOrder.verify(paint).color=defaultColor


    }


    @Test
    fun draw_link(){

        val iconColor: Int=Color.RED
        val padding: Float=8f
        val textColor: Int=Color.BLUE

        val bgColor: Int=Color.GREEN
        val cornerRadius: Float =8f

        val canvasWidth=700
        val defaultColor=Color.GRAY
        val measureText=100f
        val defaultAscent=-30
        val defaultDescent=10

        val cml=0
        val ltop=0
        val lbase=60
        val lbottom=80

        val canvas=mock(Canvas::class.java)
        `when`(canvas.width).thenReturn(canvasWidth)
        val paint=mock(Paint::class.java)
        `when`(paint.color).thenReturn(defaultColor)
        `when`(paint.measureText(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyInt(),
            ArgumentMatchers.anyInt()
        )).thenReturn(measureText)
        val layout=mock(Layout::class.java)
        val fm=mock(Paint.FontMetricsInt::class.java)
        fm.ascent=defaultAscent
        fm.descent=defaultDescent


        val linkDrawble: Drawable=spy(VectorDrawable())
        val path: Path =spy(Path())

        val text= SpannableString("text")

        val span= IconLinkSpan(linkDrawble,iconColor,padding,textColor)
        text.setSpan(span,0,text.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.path=path

        val size=span.getSize(paint,text,0,text.length,fm)
        assertEquals((2*padding+measureText).toInt(),size)

        verify(linkDrawble).setBounds(0,0,fm.descent-fm.ascent,fm.descent-fm.ascent)
        verify(linkDrawble).setTint(iconColor)

        span.draw(canvas,text,0,text.length,cml.toFloat(),ltop,lbase,lbottom,paint)

        val inOrder= inOrder(paint,canvas,path,linkDrawble)

        verify(paint,atLeastOnce()).pathEffect=any()
        verify(paint,atLeastOnce()).strokeWidth=0f
        inOrder.verify(paint).color=textColor

        verify(path).reset()
        verify(path).moveTo(cml+span.iconSize+padding,lbottom.toFloat())
        verify(path).lineTo(cml+span.iconSize+padding+span.textWidth,lbottom.toFloat())

        inOrder.verify(canvas).drawPath(path,paint)

        inOrder.verify(canvas).save()
        inOrder.verify(canvas).translate(cml.toFloat(),(lbottom-linkDrawble.bounds.bottom).toFloat())
        inOrder.verify(linkDrawble).draw(canvas)
        inOrder.verify(canvas).restore()

        inOrder.verify(paint).color=textColor
        inOrder.verify(canvas).drawText(text,0,text.length,cml+span.iconSize+padding,lbase.toFloat(),paint)
        inOrder.verify(paint).color=defaultColor

    }


}


