package ru.skillbranch.skillarticles.viewmodels

class ArticleViewModel(articleId: String): BaseViewModel<ArticleState>(ru.skillbranch.skillarticles.viewmodels.ArticleState()){
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