package com.example.mvibase

import androidx.annotation.CallSuper
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.*
import com.example.mvibase.stores.BaseStore
import io.reactivex.disposables.CompositeDisposable
import java.lang.reflect.Constructor

abstract class BaseViewModel<A : IAction, R>(protected val store : BaseStore<A, R>) : ViewModel(), Observer<R> {

    protected val mDisposable by lazy { CompositeDisposable() }

    val isLoading = ObservableBoolean(false)

    init {
        store.subscribeObserver(this)
    }

    override fun onChanged(t: R) {
        if (t == null) return
        render(t)
    }

    open fun render(state: R) {}

    protected fun dispatch(action: A) {
        store.dispatch(action)
    }

    @CallSuper
    override fun onCleared(){
        mDisposable.clear()
        store.unsubscribeObserver(this)
    }

    open class BaseViewModelFactory<A : IAction, R>(protected val store: BaseStore<A, R>) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val constructor: Constructor<T> = modelClass.getDeclaredConstructor(BaseStore::class.java)
            return constructor.newInstance(store)
        }
    }

}