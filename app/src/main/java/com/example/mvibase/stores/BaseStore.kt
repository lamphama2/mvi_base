package com.example.mvibase.stores

import androidx.lifecycle.*
import com.example.mvibase.IAction
import com.example.mvibase.Dispatcher
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

abstract class BaseStore<A : IAction, R> : ViewModel() {

    private val dispatcher by lazy { Dispatcher(this) }

    private val state by lazy { MutableLiveData<R>() }

    /**
     * The most important method of the class. This holds all the business logic of application.
     * Basically, it receives intent A (in form of a [Observable]) and results a view state R
     */
    abstract fun Observable<A>.reduce(): Observable<R>

    internal fun subscribeObserver(scope: LifecycleOwner, render: (R) -> Unit) {
        state.observe(scope, Observer {
            it?.let { render(it) }
        })
    }

    internal fun subscribeObserver(observer : Observer<R>) {
        state.observeForever(observer)
    }

    internal fun unsubscribeObserver(scope: LifecycleOwner) {
        state.removeObservers(scope)
    }

    internal fun unsubscribeObserver(observer : Observer<R>) {
        state.removeObserver(observer)
    }

    /**
     * Result is not kept after released by [LiveData]
     */
    internal fun subscribeDispatcher(dispatcher: Observable<A>, type: DISPATCHER_TYPE): Disposable {
        val scheduler = when (type) {
            DISPATCHER_TYPE.UI -> AndroidSchedulers.mainThread()
            DISPATCHER_TYPE.IO -> Schedulers.io()
            DISPATCHER_TYPE.COMPUTE -> Schedulers.computation()
        }

        return dispatcher.subscribe {
            Observable.just(it)
                .reduce()
                .subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    state.value = it
                    state.value = null
                }
        }
    }

    fun dispatch(action: A) {
        dispatcher.dispatch(action)
    }

    override fun onCleared() {
        dispatcher.clear()
        super.onCleared()
    }
}


enum class DISPATCHER_TYPE {
    IO, UI, COMPUTE
}