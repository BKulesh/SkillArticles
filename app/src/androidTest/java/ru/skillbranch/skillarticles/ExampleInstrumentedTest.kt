package ru.skillbranch.skillarticles

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
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
import org.mockito.Mockito.*
import ru.skillbranch.skillarticles.data.LocalDataHolder
import ru.skillbranch.skillarticles.data.NetworkDataHolder
import ru.skillbranch.skillarticles.extensions.indexesOf
import ru.skillbranch.skillarticles.extensions.setMarginOptionally
import ru.skillbranch.skillarticles.markdown.spans.UnorderedListSpan
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

}


