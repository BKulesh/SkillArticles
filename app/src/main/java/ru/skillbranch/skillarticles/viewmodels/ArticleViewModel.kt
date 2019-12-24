package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.LiveData
import kotlinx.android.synthetic.main.layout_bottombar.*
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.NetworkDataHolder.content
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.format

class ArticleViewModel(private val articleId: String): BaseViewModel<ArticleState>(ru.skillbranch.skillarticles.viewmodels.ArticleState()){
    private val repository =ArticleRepository

    init {
            subscribeOnDataSource(getArticleData()) {article,state->
                article?:return@subscribeOnDataSource null
                state.copy(
                    shareLink = article.shareLink,
                    title=article.title,
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
                isBookMark = info.isBookmark,
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

    private fun getArticleContent():LiveData<List<Any>?>
    {
        return repository.loadArticleContent(articleId)
    }

    private fun getArticlePersonalInfo():LiveData<ArticlePersonalInfo?>{
        return repository.loadArticlePersonalInfo(articleId)
    }

    fun handleUpText() {}
    fun handleDownText() {}
    fun handleNightMode() {}

    fun handleLike(){}
    fun handleBookmark() {}
    fun handleShare() {}
    fun handleToogleMenu() {
        updateState { it.copy(isShowMenu = !it.isShowMenu) }
    }


}


data class ArticleState(
    val isAuth:Boolean= false,// пользоватедь авторизован
    val isLoadingContent: Boolean=true,//контент загружается
    val isLoadingReviews:Boolean=true,//отзывы загружаются
    val isLike:Boolean=false,//отмечено как Like
    val isBookMark: Boolean=false,//в закладках
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
    val content: List<Any> = emptyList(), // содержание
    val reviews: List<Any> = emptyList() // комментарии
)