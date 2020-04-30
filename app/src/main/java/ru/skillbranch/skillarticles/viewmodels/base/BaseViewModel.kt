package ru.skillbranch.skillarticles.viewmodels.base

import androidx.annotation.UiThread
import androidx.lifecycle.*
import androidx.lifecycle.SavedStateHandle

abstract class BaseViewModel<T:IViewModelState>(
   private val handleState: SavedStateHandle,
   initState: T
): ViewModel(){
    public val notifications= MutableLiveData<Event<Notify>>()

    public val state: MediatorLiveData<T> = MediatorLiveData<T>().apply{
       value=initState
    }

    protected val currentState
    get() = state.value!!


    @UiThread
    inline fun updateState(update:(currentState:T)->T){
        val updatedState:T=update(currentState)
        state.value=updatedState
    }

    @UiThread
    protected fun notify(content: Notify){
        notifications.value=
            Event(content)
    }

    fun observeState(owner: LifecycleOwner,onChanged: (newState: T)->Unit ){
        state.observe(owner, Observer{onChanged(it!!)})
    }

    fun observeNotifications(owner:LifecycleOwner,onNotify:(notification: Notify)->Unit){
        notifications.observe(owner,
            EventObserver {
                onNotify(it)
            })

    }

    protected fun <S> subscribeOnDataSource(
        source: LiveData<S>,
        onChanged: (newValue: S,currentState:T) -> T?
    ){
        state.addSource(source) {
            state.value=onChanged(it,currentState)?: return@addSource
        }
    }

    fun saveState(){
        currentState.save(handleState)
    }

    @Suppress("UNCHECKED_CAST")
    fun restoreState(){
        state.value=currentState.restore(handleState) as T
    }

}



/*class ViewModelFactory(private val params: String): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
            return ArticleViewModel(
                params
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}*/

class Event<out E>(private val content:E){
    var hasBeenHandled = false
    //var message: String=content.


    fun peekContent(): E =content

    fun getContentIfNotHandled():E?{
        return if (hasBeenHandled) null
        else {
            hasBeenHandled=true
            content
        }
    }

}




class EventObserver<E>(private val onEventUnhandledContent:(E)->Unit):Observer<Event<E>>{
    override fun onChanged(event: Event<E>?) {
        event?.getContentIfNotHandled()?.let{
            onEventUnhandledContent(it)
        }
    }
}



sealed class Notify(){
    abstract val message: String
    data class TextMessage(override val message:String): Notify()

    data class ActionMessage(
        override val message: String,
        val actionLabel: String,
        val actionHandler: (()->Unit)?
    ): Notify()

    data class ErrorMessage(
        override val message: String,
        val errLabel: String,
        val errHandler: (()->Unit)?
    ): Notify()
}