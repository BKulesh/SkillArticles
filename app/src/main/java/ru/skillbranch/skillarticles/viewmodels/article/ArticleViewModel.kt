package ru.skillbranch.skillarticles.viewmodels.article

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.skillarticles.data.AppSettings
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format
import ru.skillbranch.skillarticles.extensions.indexesOf
import ru.skillbranch.skillarticles.data.repositories.clearContent
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.Notify

class ArticleViewModel(handle:SavedStateHandle,
                       private val articleId: String): BaseViewModel<ArticleState>(handle,
    ArticleState()
){
    private val repository =ArticleRepository
    private var clearContent: String?=null

    init {
            subscribeOnDataSource(getArticleData()) {article,state->
                article?:return@subscribeOnDataSource null
                state.copy(
                    shareLink = article.shareLink,
                    title=article.title,
                    author=article.author,
                    category = article.category,
                    categoryIcon = article.categoryIcon,
                    date=article.date.format()
                )
            }


        subscribeOnDataSource(getArticleContent()) { content,state->
            content?:return@subscribeOnDataSource null
            state.copy(
                isLoadingContent = false,
                content = content
            )

        }

        subscribeOnDataSource(getArticlePersonalInfo()) { info,state->
            info?:return@subscribeOnDataSource null
            state.copy(
                isBookmark = info.isBookmark,
                isLike = info.isLike
            )

        }

        subscribeOnDataSource(repository.getAppSettings()){settings,state->
            state?:return@subscribeOnDataSource null
            state.copy(
                isDarkMode = settings.isDarkMode,
                isBigText=settings.isBigText
            )

        }


    }

    private fun getArticleData():LiveData<ArticleData?>{
        return repository.getArticle(articleId)

    }

    private fun getArticleContent():LiveData<List<MarkdownElement>?>
    {
        return repository.loadArticleContent(articleId)
    }

    private fun getArticlePersonalInfo():LiveData<ArticlePersonalInfo?>{
        return repository.loadArticlePersonalInfo(articleId)
    }

    private fun getAppSettings():LiveData<AppSettings>{
        return repository.getAppSettings()
    }


    fun handleUpText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText=true))
    }
    fun handleDownText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText=false))
    }

    fun handleCopyCode(){
        notify(Notify.TextMessage("Code copy to clipboard."))
    }

    fun handleNightMode() {
        val settings=currentState.toAppSettings()
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode))
    }

    fun handleLike(){

        val toggleLike:()->Unit={
            val info=currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isLike = !info.isLike))
        }
        toggleLike()

        val msg=if (currentState.isLike) Notify.TextMessage("Mark is liked")
        else {
            Notify.ActionMessage(
                "Don't like it anymore",
                "No, still like it",
                toggleLike
            )
        }

        notify(msg)
    }
    fun handleBookmark() {

        val toggleBookmark:()->Unit={
            val info=currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isBookmark = !info.isBookmark))
        }
        toggleBookmark()

        val msg=if (currentState.isBookmark) Notify.TextMessage("Add to bookmarks")
        else {
            Notify.ActionMessage(
                "Remove from bookmarks",
                "Not yet your bookmark",
                toggleBookmark
            )
        }

        notify(msg)

    }


    fun handleShare() {
        val msg="Share is not implemented"
        notify(Notify.ErrorMessage(msg,"OK",null))
    }
    fun handleToggleMenu() {
        updateState { it.copy(isShowMenu = !it.isShowMenu) }
    }


    fun handleSearchMode(isSearch: Boolean){
        updateState { it.copy(isSearch=isSearch,isShowMenu = false,searchPosition = 0) }
    }

    fun handleSearch(query: String?){
        query ?: return
        //Log.e("Debug","handleSearch currentState.content="+currentState.content)
        if (clearContent==null && currentState.content.isNotEmpty()) clearContent=currentState.content.clearContent()
        //Log.e("Debug","handleSearch currentState.content="+currentState.content)
        //if (currentState.content.isNullOrEmpty()) Log.e("Debug","handleSearch currentState.content=null")
        Log.e("Debug","handleSearch clearContent="+clearContent)
        val result=clearContent
            .indexesOf(query)
            .map{it to it + query.length}
        //Log.e("Debug","handleSearch clearContent="+clearContent)
        //result.forEach { Log.e("Debug"," handleSearch massive "+it.toString()) }
        //Log.e("Debug","after handleSearch")
        updateState { it.copy(searchQuery= query,searchResults = result,searchPosition = 0) }
        //updateState { it.copy(searchQuery= query) }

    }

    fun handleUpResult() {
        updateState { it.copy(searchPosition = it.searchPosition.dec()) }
    }

    fun handleDownResult() {
        updateState { it.copy(searchPosition = it.searchPosition.inc()) }
    }


}




data class ArticleState(
    val isAuth:Boolean= false,// пользоватедь авторизован
    val isLoadingContent: Boolean=true,//контент загружается
    val isLoadingReviews:Boolean=true,//отзывы загружаются
    val isLike:Boolean=false,//отмечено как Like
    val isBookmark: Boolean=false,//в закладках
    val isShowMenu: Boolean=false,//отображается меню
    val isBigText:Boolean=false,//Шрифт увеличен
    val isDarkMode:Boolean=false,//Темный режим
    val isSearch:Boolean=false,//Режим поисска
    val searchQuery: String?=null,//поисковый запрос
    val searchResults:List<Pair<Int,Int>> = emptyList(),//Результаты поиска
    val searchPosition:Int=0,//Текущая позиция найденного результата
    val shareLink: String?=null,//ссылка Share
    val title: String?=null,//заголовок статьи
    val category: String?=null,//категория
    val categoryIcon: Any?=null,//иконка категории
    val date: String?=null,//дата публикации
    val poster: String?=null,//обложка статьи
    val author: Any?=null,//автор статьи
    val content: List<MarkdownElement> = emptyList(), // содержание
    val reviews: List<Any> = emptyList() // комментарии
): IViewModelState{


    override fun save(outState: SavedStateHandle) {
        Log.e("Debug","IViewModelState save $searchResults")
                outState.set("isSearch" , isSearch)
                outState.set("searchQuery" , searchQuery)
                outState.set("searchResults" , searchResults)
                outState.set("searchPosition" , searchPosition)
    }

    override fun restore(savedState: SavedStateHandle): IViewModelState {
        Log.e("Debug","IViewModelState restore ")
        return copy(
            isSearch=savedState["isSearch"] ?: false,
            searchQuery = savedState["searchQuery"],
            searchResults = savedState["searchResults"] ?: emptyList(),
            searchPosition = savedState["searchPosition"] ?: 0
        )
    }
}