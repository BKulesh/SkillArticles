package ru.skillbranch.skillarticles.ui.article

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.layout_bottombar.*
import kotlinx.android.synthetic.main.layout_submenu.*
import kotlinx.android.synthetic.main.search_view_layout.*

import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.hideKeyboard
import ru.skillbranch.skillarticles.ui.RootActivity
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.ui.base.Binding
import ru.skillbranch.skillarticles.ui.delegates.RenderProp
import ru.skillbranch.skillarticles.viewmodels.article.ArticleState
import ru.skillbranch.skillarticles.viewmodels.article.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.ViewModelFactory

class ArticleFragment : BaseFragment<ArticleViewModel>(),IArticleView {
    private val args : ArticleFragmentArgs by navArgs()

    override val viewModel: ArticleViewModel by viewModels {
        ViewModelFactory(
            owner = this,
            params = args.articleId
        )
    }

    override val layout: Int = R.layout.fragment_article
    public override val binding: ArticleBinding by lazy { ArticleBinding() }

    /*override fun setupViews() {
        TODO("Not yet implemented")
    }

    override fun showSearchBar() {
        TODO("Not yet implemented")
    }

    override fun hideSearchBar() {
        TODO("Not yet implemented")
    }*/


    override fun setupViews() {
        setupBottomBar()
        setupSubMenu()
    }

    override fun showSearchBar() {
        //bottombar.setSearchState(true)
        //scroll.setMarginOptionally(dpToIntPx(56))
    }

    override fun hideSearchBar() {
        //bottombar.setSearchState(false)
        //scroll.setMarginOptionally(dpToIntPx(0))
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        /*menuInflater.inflate(R.menu.menu_search,menu)
        val menuItem=menu?.findItem(R.id.action_search)
        val searchView=(menuItem?.actionView as? SearchView)
        searchView?.queryHint=getString(R.string.article_search_placeholder)


        if (binding.isSearch){
            menuItem?.expandActionView()
            searchView?.setQuery(binding.searchQuery,false)

            if (binding.isFocusedSearch) searchView?.requestFocus()
            else searchView?.clearFocus()
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


        //return super.onPrepareOptionsMenu(menu)*/
    }


    private fun setupSubMenu() {
        /*btn_text_up.setOnClickListener{ viewModel.handleUpText() }
        btn_text_down.setOnClickListener{ viewModel.handleDownText() }
        switch_mode.setOnClickListener{ viewModel.handleNightMode() }*/
    }


    private fun setupBottomBar() {
/*        btn_like.setOnClickListener{ viewModel.handleLike()}
        btn_bookmark.setOnClickListener{viewModel.handleBookmark()}
        btn_share.setOnClickListener{viewModel.handleShare()}
        btn_settings.setOnClickListener{viewModel.handleToggleMenu()}

        btn_result_up.setOnClickListener{
            //if (search_view.hasFocus()) search_view.clearFocus()
            if (!tv_text_content.hasFocus()) tv_text_content.requestFocus()
            root.hideKeyboard(btn_result_up)
            viewModel.handleUpResult()
        }

        btn_result_down.setOnClickListener{
            //if (search_view.hasFocus()) search_view.clearFocus()
            if (!tv_text_content.hasFocus()) tv_text_content.requestFocus()
            root.hideKeyboard(btn_result_down)
            viewModel.handleDownResult()
        }

        btn_search_close.setOnClickListener{
            viewModel.handleSearchMode(false)
            root.invalidateOptionsMenu()
        }*/
    }

    private fun setupCopyListener() {
        tv_text_content.setCopyListener { copy ->
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied code", copy)
            clipboard.setPrimaryClip(clip)
            viewModel.handleCopyCode()
        }
    }

    inner class ArticleBinding() : Binding() {
        var isFocusedSearch: Boolean = false

        //private var isSearching: Boolean=false
        var searchQuery: String? = null

        private var isLoadingContent by RenderProp(true)

        /*        private var isLike: Boolean by RenderProp(false) { btn_like.isChecked = it }
        private var isBookMark: Boolean by RenderProp(false) { btn_bookmark.isChecked = it }
        private var isShowMenu: Boolean by RenderProp(false) {
            btn_settings.isChecked = it
//            if (it) submenu.open() else submenu.close()
        }


        private var isBigText: Boolean by RenderProp(false) {
            if (it) {
                tv_text_content.textSize = 18f
                btn_text_up.isChecked = true
                btn_text_down.isChecked = false
            } else {
                tv_text_content.textSize = 14f
                btn_text_up.isChecked = false
                btn_text_down.isChecked = true
            }
        }

        private var isDarkMode: Boolean by RenderProp(false, false) {
            switch_mode.isChecked = it
            root.delegate.localNightMode = if (it) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        }
*/
        var isSearch: Boolean by RenderProp(false) {
            /*    if (it) {
                showSearchBar()
                with(toolbar) {
                    (layoutParams as AppBarLayout.LayoutParams).scrollFlags =
                        AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
                }
            } else {
                hideSearchBar()
                with(toolbar) {
                    (layoutParams as AppBarLayout.LayoutParams).scrollFlags =
                        AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                                AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED
                }
            }*/
        }

        private var searchResults: List<Pair<Int, Int>> by RenderProp(emptyList())
        private var searchPosition: Int by RenderProp(0)

        private var content: List<MarkdownElement> by RenderProp(emptyList()) {
            tv_text_content.setContent(it)
            if (it.isNotEmpty()) {
                setupCopyListener()
            }
        }

        override val afterInFlated: (() -> Unit)? = {
            dependsOn<Boolean, Boolean, List<Pair<Int, Int>>, Int>(
                ::isLoadingContent,
                ::isSearch,
                ::searchResults,
                ::searchPosition
            ) { ilc, iss, sr, sp ->
                if (!ilc && iss) {
                    tv_text_content.renderSearchResult(sr)
                    tv_text_content.renderSearchPosition(sr.getOrNull(sp))
                }
                if (!ilc && !iss) {
                    tv_text_content.clearSearchResult()
                }
                //bottombar.bindSearchInfo(sr.size, sp)
            }

        }


        override fun bind(data: IViewModelState) {
            data as ArticleState
            /*isLike = data.isLike
        isBookMark = data.isBookmark
        isShowMenu = data.isShowMenu
        isBigText = data.isBigText
        isDarkMode = data.isDarkMode*/

            //if (data.title != null) title = data.title
            //if (data.category != null) category = data.category
            //if (data.categoryIcon != null) categoryIcon = data.categoryIcon as Int
            if (data.content != null) content = data.content

            isLoadingContent = data.isLoadingContent
            isSearch = data.isSearch
            searchQuery = data.searchQuery
            searchPosition = data.searchPosition
            searchResults = data.searchResults
        }

        override fun saveUI(outState: Bundle) {
            outState.putBoolean(::isFocusedSearch.name, search_view?.hasFocus() ?: false)
        }

        override fun restoreUI(savedState: Bundle?) {
            isFocusedSearch = savedState?.getBoolean(::isFocusedSearch.name) ?: false
        }


    }
}