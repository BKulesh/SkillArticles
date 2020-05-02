package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.isEmpty
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.forEach
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.groupByBounds
import ru.skillbranch.skillarticles.extensions.setPaddingOptionally
import kotlin.properties.Delegates

class MarkdownContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private lateinit var elements: List<MarkdownElement>
    private var layoutManager: LayoutManager= LayoutManager()

    //for restore
    //public var ids = arrayListOf<Int>()

    var textSize by Delegates.observable(14f) {_,old,value->
        if (value==old) return@observable
        children.forEach {
            it as IMarkdownView
            it.fontSize=value
        }

    }
    var isLoading: Boolean = true
    val padding=context.dpToIntPx(8) //8dp

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedHeight=paddingTop
        val width= View.getDefaultSize(suggestedMinimumWidth,widthMeasureSpec)

        children.forEach {
            measureChild(it,widthMeasureSpec,heightMeasureSpec)
            usedHeight+=it.measuredHeight
        }

        usedHeight+=paddingBottom
        setMeasuredDimension(width,usedHeight)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var usedHeight=paddingTop
        val bodyWith=right-left-paddingLeft-paddingRight
        val left=paddingLeft
        val right=paddingLeft+bodyWith

        children.forEach {
            if(it is MarkdownTextView) {
                it.layout(
                    left-paddingLeft/2,
                    usedHeight,
                    r-paddingRight/2,
                    usedHeight+it.measuredHeight
                )
            } else {
                it.layout(
                    left,
                    usedHeight,
                    r,
                    usedHeight + it.measuredHeight
                )
            }
            usedHeight+=it.measuredHeight
        }
    }

    fun setContent(content: List<MarkdownElement>) {
        Log.e("Debug","View on setContent start")
        elements=content
        var index=0
        content.forEach{
            when(it){
                is MarkdownElement.Text->{
                    val tv=MarkdownTextView(context,textSize).apply {
                        setPaddingOptionally(left=context.dpToIntPx(8),right=context.dpToIntPx(8))
                        setLineSpacing(fontSize*0.5f,1f)
                    }
                    MarkdownBuilder(context)
                        .markdownToSpan(it)
                        .run{
                            tv.setText(this,TextView.BufferType.SPANNABLE)
                        }
                    //tv.id=ids.count()+1
                    addView(tv)
                    //ids.add(0)
                }
                is MarkdownElement.Image -> {
                    val iv=MarkdownImageView(
                        context,
                        textSize,
                        it.image.url,
                        it.image.text,
                        it.image.alt
                    )
                    //iv.id=ids.count()+1
                    addView(iv)
                    //ids.add(0)
                    layoutManager.attacheToParent(iv,index)
                    index++
                }
                is MarkdownElement.Scroll -> {
                    val sv=MarkdownCodeView(
                        context,
                        textSize,
                        it.blockCode.text//,padding,padding.toFloat()
                    )
                    //sv.id=ids.count()+1
                    addView(sv)
                    //layoutManager.attacheToParent(sv,index)
                    //index++
                    //ids.add(0)
                }
            }
        }
        Log.e("Debug","View on setContent finish")
    }

    fun renderSearchResult(searchResult: List<Pair<Int, Int>>) {
        children.forEach {view->
            view as IMarkdownView
            view.clearSearchResult()
        }

        if (searchResult.isEmpty()) return

        val bounds=elements.map { it.bounds }
        val result=searchResult.groupByBounds(bounds)

        children.forEachIndexed{index,view->
            view as IMarkdownView
            //view.renderSearchResult(result[index],elements[index].offset)
            //view.renderSearchResult(listOf(27 to 65), 0)
        }
    }

    fun renderSearchPosition(
        searchPosition: Pair<Int, Int>?
    ) {
        searchPosition?: return
        val bounds=elements.map { it.bounds }

        val index=bounds.indexOfFirst { (start, end) ->
            val boundRange= start..end
            val (startPos,endPos)=searchPosition
            startPos in boundRange && endPos in boundRange
        }

        if (index==-1) return
        val view=getChildAt(index)
        view as IMarkdownView
        view.renderSearchPosition(searchPosition,elements[index].offset)

    }

    fun clearSearchResult() {
        children.forEach {view->
            view as IMarkdownView
            view.clearSearchResult()
        }
    }

    fun setCopyListener(listener: (String) -> Unit) {
        children.filterIsInstance<MarkdownCodeView>()
            .forEach { it.copyListener=listener }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val state = SavedState(super.onSaveInstanceState())
        state.layout=layoutManager
        Log.e("Debug","ContentView onSave ")
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        Log.e("Debug","ContentView onRestore Satrt ")
        super.onRestoreInstanceState(state)
        if (state is SavedState) layoutManager=state.layout
        children.filter { it !is MarkdownTextView }
            .forEachIndexed {index,it-> layoutManager.attacheToParent(it,index) }
        Log.e("Debug","ContentView onRestore Finish")
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>?) {
        Log.e("Debug","ContentView onDispatchSave Start")
        children.filter { it !is MarkdownTextView }
            .forEachIndexed {index,it->layoutManager.attacheToParent(it,index) }
        Log.e("Debug","ContentView onDispatchSave Middle")
        children.filter { it !is MarkdownTextView }
            .forEach { it.saveHierarchyState(layoutManager.container) }
        //super.dispatchSaveInstanceState(container)
        dispatchFreezeSelfOnly(container)
        Log.e("Debug","ContentView onDispatchSave Finish")
    }

    private class LayoutManager(): Parcelable {
        var ids: MutableList<Int> = mutableListOf()
        var container: SparseArray<Parcelable> = SparseArray()

    constructor(parcel: Parcel): this(){
        ids=parcel.readArrayList(Int::class.java.classLoader) as ArrayList<Int>
        container=parcel.readSparseArray<Parcelable>(this::class.java.classLoader) as SparseArray<Parcelable>
    }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeIntArray(ids.toIntArray())
            parcel.writeSparseArray(container)
        }


        fun attacheToParent(view: View,index: Int) {
            if (container.isEmpty()){
                view.id= ViewCompat.generateViewId()
                ids.add(view.id)
                Log.e("Debug","View on add Id ${view.id} ")
            } else {
                view.id=ids[index]
                Log.e("Debug","View on set Id ${view.id} ")
                view.restoreHierarchyState(container)
            }
        }

        override fun describeContents(): Int =0

        companion object CREATOR: Parcelable.Creator<LayoutManager>{
            override fun createFromParcel(parcel: Parcel): LayoutManager= LayoutManager(parcel)
            override fun newArray(size: Int): Array<LayoutManager?> = arrayOfNulls(size)
            }
        }

    private class SavedState: BaseSavedState,Parcelable{
        lateinit var layout: LayoutManager

        constructor(superState: Parcelable?): super(superState)

        @Suppress("UNCHECKED_CAST")
        constructor(src: Parcel): super(src) {
            layout=src.readParcelable(LayoutManager::class.java.classLoader)!!
        }

        override fun writeToParcel(dst: Parcel, flags: Int) {
            super.writeToParcel(dst, flags)
            dst.writeParcelable(layout,flags)
        }

        override fun describeContents(): Int =0

        companion object CREATOR: Parcelable.Creator<LayoutManager>{
            override fun createFromParcel(parcel: Parcel): LayoutManager= LayoutManager(parcel)
            override fun newArray(size: Int): Array<LayoutManager?> = arrayOfNulls(size)
        }


    }

    }


