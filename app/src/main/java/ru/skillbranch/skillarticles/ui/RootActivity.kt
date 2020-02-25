package ru.skillbranch.skillarticles.ui

import android.graphics.Color
import android.os.Bundle
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
//import android.widget.Toolbar
import androidx.appcompat.widget.Toolbar
import androidx.core.text.getSpans
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.layout_bottombar.*
import kotlinx.android.synthetic.main.layout_submenu.*
import kotlinx.android.synthetic.main.search_view_layout.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.ui.base.BaseActivity
import ru.skillbranch.skillarticles.ui.base.Binding
import ru.skillbranch.skillarticles.ui.custom.SearchFocusSpan
import ru.skillbranch.skillarticles.ui.custom.SearchSpan
import ru.skillbranch.skillarticles.ui.delegates.AttrValue
import ru.skillbranch.skillarticles.ui.delegates.ObserveProp
import ru.skillbranch.skillarticles.ui.delegates.RenderProp
import ru.skillbranch.skillarticles.viewmodels.*
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.Notify
import ru.skillbranch.skillarticles.viewmodels.base.ViewModelFactory

class RootActivity : BaseActivity<ArticleViewModel>(),
    IArticleView {
    //      constructor()
    override val layout: Int = R.layout.activity_root
    override val viewModel: ArticleViewModel by lazy{
        val vmFactory=ViewModelFactory("0")
        ViewModelProviders.of(this,vmFactory).get(ArticleViewModel::class.java)
    }
    override val binding: Binding by lazy { ArticleBinding() }

    private var isSearching: Boolean=false
    private var searchQuery: String?= null

    private val bgColor by AttrValue(R.attr.colorSecondary)
    private val fgColor by AttrValue(R.attr.colorOnSecondary)

/*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vmFactory=
            ViewModelFactory("0")
        viewModel=ViewModelProviders.of(this,vmFactory).get(ArticleViewModel::class.java)
        viewModel.observeState(this) {
            renderUi(it)
        }

        viewModel.observeNotifications(this){
            renderNotification(it)
        }

    }
*/




    override fun setupViews() {
        setupToolbar()
        setupBottomBar()
        setupSubMenu()
    }

    override fun renderSearchResult(searchResult: List<Pair<Int, Int>>) {
        Log.e("Debug","renderSearchResult before")
        val content=tv_text_content.text as Spannable
        Log.e("Debug","renderSearchResult after")
        clearSearchResult()
        //val bgColor= Color.RED
        //val fgColor=Color.WHITE
        searchResult.forEach{(start,end)->
            content.setSpan(SearchSpan(bgColor,fgColor),start,end,SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        renderSearchPosition(0)
    }

    override fun renderSearchPosition(searchPosition: Int) {
        val content=tv_text_content.text as Spannable

        val spans=content.getSpans<SearchSpan>()
        content.getSpans<SearchFocusSpan>().forEach { content.removeSpan(it) }

        if (spans.isNotEmpty()){
            val result=spans[searchPosition]
            Selection.setSelection(content,content.getSpanStart(result))
            content.setSpan(SearchFocusSpan(bgColor, fgColor),
                content.getSpanStart(result),content.getSpanEnd(result),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    override fun clearSearchResult() {
        val content=tv_text_content.text as Spannable
        content.getSpans<SearchSpan>()
            .forEach { content.removeSpan(it) }
    }

    override fun showSearchBar() {
        bottombar.setSearchState(true)
        //scroll.setMarginOptionally(dpToIntPx(56))
    }

    override fun hideSearchBar() {
        bottombar.setSearchState(false)
        //scroll.setMarginOptionally(dpToIntPx(0))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search,menu)
        val menuItem=menu?.findItem(R.id.action_search)
        val searchView=(menuItem?.actionView as? SearchView)
        searchView?.queryHint=getString(R.string.article_search_placeholder)


        if (isSearching){
            menuItem?.expandActionView()
            searchView?.setQuery(searchQuery,false)
            //searchView?.clearFocus()
        }

        //return super.onCreateOptionsMenu(menu)

        menuItem?.setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                Log.d("menuitem","collapse")
                viewModel.handleSearchMode(false)
                return true
            }

            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                Log.d("menuitem","expande")
                viewModel.handleSearchMode(true)
                return true
            }
        })
        searchView?.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearch(query)
                //searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearch(newText)
                //searchView.clearFocus()
                return true
            }

        })


        return super.onCreateOptionsMenu(menu)
    }

    override fun renderNotification(notify: Notify){
        val snackbar=Snackbar.make(coordinator_container,notify.message,Snackbar.LENGTH_LONG)
            snackbar.setAnchorView(bottombar)
            snackbar.setActionTextColor(getColor(R.color.color_accent_dark))

        when (notify){
            is  Notify.TextMessage -> {}
            is Notify.ActionMessage -> {
                snackbar.setAction(notify.actionLabel){notify.actionHandler?.invoke()}
            }
            is Notify.ErrorMessage -> {
                with(snackbar){
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel){
                        notify.errHandler?.invoke()
                    }
                }
            }
        }

        snackbar.show()

    }

    private fun setupSubMenu() {
        btn_text_up.setOnClickListener{ viewModel.handleUpText() }
        btn_text_down.setOnClickListener{ viewModel.handleDownText() }
        switch_mode.setOnClickListener{ viewModel.handleNightMode() }
        }


    private fun setupBottomBar() {
        btn_like.setOnClickListener{ viewModel.handleLike()}
        btn_bookmark.setOnClickListener{viewModel.handleBookmark()}
        btn_share.setOnClickListener{viewModel.handleShare()}
        btn_settings.setOnClickListener{viewModel.handleToggleMenu()}

        btn_result_up.setOnClickListener{
            if (search_view.hasFocus()) search_view.clearFocus()
            viewModel.handleUpResult()
        }

        btn_result_down.setOnClickListener{
            if (search_view.hasFocus()) search_view.clearFocus()
            viewModel.handleDownResult()
        }

        btn_search_close.setOnClickListener{
            viewModel.handleSearchMode(false)
            invalidateOptionsMenu()
        }
    }

    /*
    private fun renderUi(data:ArticleState){
        Log.e("RootActivity","renderUi:$data")
        //bottombar.setSearchState(data.isSearch)
        if (data.isSearch) showSearchBar() else hideSearchBar()

        if (data.searchResults.isNotEmpty()) renderSearchResult(data.searchResults)
        if (data.searchResults.isNotEmpty()) renderSearchPosition(data.searchPosition)


        btn_settings.isChecked=data.isShowMenu
        if (data.isShowMenu) submenu.open() else submenu.close()

        btn_like.isChecked=data.isLike
        btn_bookmark.isChecked=data.isBookmark

        switch_mode.isChecked=data.isDarkMode

        delegate.localNightMode=
            if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else  AppCompatDelegate.MODE_NIGHT_NO

        if (data.isBigText) {
            tv_text_content.textSize=18f
            btn_text_up.isChecked = true
            btn_text_down.isChecked = false
        } else {
            tv_text_content.textSize=14f
            btn_text_up.isChecked = false
            btn_text_down.isChecked = true
        }

        //tv_text_content.text= if (data.isLoadingContent) "loading" else data.content.first() as String

        if (data.isLoadingContent) {
            tv_text_content.text="loading"
        } else if (tv_text_content.text=="loading") {
            val content=data.content.first() as String;
            tv_text_content.setText(content,TextView.BufferType.SPANNABLE)
            tv_text_content.movementMethod=ScrollingMovementMethod()
        }
        toolbar.title= data.title?: "loading"
        toolbar.subtitle=data.category?:"loading"
        if (data.categoryIcon!=null) toolbar.logo=getDrawable(data.categoryIcon as Int)

    }*/


    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val logo:ImageView?= if(toolbar.childCount>2) toolbar.getChildAt(2) as ImageView else null
        logo?.scaleType=ImageView.ScaleType.CENTER_CROP
        val lp=logo?.layoutParams as  Toolbar.LayoutParams
        lp.let {
            it.width=this.dpToIntPx(40)
            it.height=this.dpToIntPx(40)
            it.marginEnd=this.dpToIntPx(16)
            logo.layoutParams=it
        }

    }

    inner class  ArticleBinding(): Binding(){

        private var isLoadingContent by ObserveProp(true)

        private var isLike:Boolean by RenderProp(false){btn_like.isChecked=it}
        private var isBookMark:Boolean by RenderProp(false){btn_bookmark.isChecked=it}
        private var isShowMenu:Boolean by RenderProp(false){
            btn_settings.isChecked=it
            if (it) submenu.open() else submenu.close()
        }

        private var title:String by RenderProp("loading") {toolbar.title=it}
        private var category: String by RenderProp("loading") {toolbar.subtitle=it}
        private var categoryIcon: Int by RenderProp(R.drawable.logo_placeholder) {toolbar.logo=getDrawable(it)}

        private var isBigText:Boolean by RenderProp(false){
            if (it) {
                tv_text_content.textSize=18f
                btn_text_up.isChecked=true
                btn_text_down.isChecked=false
            }
            else {
                tv_text_content.textSize=14f
                btn_text_up.isChecked=false
                btn_text_down.isChecked=true
            }
        }

        private var isDarkMode: Boolean by RenderProp(false,false){
            switch_mode.isChecked=it
            delegate.localNightMode= if (it) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        }

        var isSearch: Boolean by ObserveProp(false){
            if (it) showSearchBar() else hideSearchBar()
        }

        private var searchResults: List<Pair<Int,Int>> by ObserveProp(emptyList())
        private var searchPosition: Int by ObserveProp(0)

        private var content: String by ObserveProp("loading"){
            tv_text_content.setText(it,TextView.BufferType.SPANNABLE)
            tv_text_content.movementMethod=ScrollingMovementMethod()
        }

        override fun onFininishInflate() {
            dependsOn<Boolean,Boolean,List<Pair<Int,Int>>,Int>(
                ::isLoadingContent,
                ::isSearch,
                ::searchResults,
                ::searchPosition
            ){ilc,iss,sr,sp->
                if (!ilc && iss) {
                    renderSearchResult(sr)
                    renderSearchPosition(sp)
                }

                bottombar.bindSearchInfo(sr.size,sp)
            }

        }

        override fun bind(data: IViewModelState) {
            data as ArticleState
            isLike=data.isLike
            isBookMark=data.isBookmark
            isShowMenu=data.isShowMenu
            isBigText=data.isBigText
            isDarkMode=data.isDarkMode

            if (data.title!=null) title=data.title
            if (data.category!=null) category=data.category
            if (data.categoryIcon!=null) categoryIcon=data.categoryIcon as Int

            isLoadingContent=data.isLoadingContent
            isSearch=data.isSearch
            searchQuery=data.searchQuery
            searchPosition=data.searchPosition
            searchResults=data.searchResults
        }

    }

}

