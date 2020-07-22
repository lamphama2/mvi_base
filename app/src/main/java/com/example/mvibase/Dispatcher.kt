package com.example.mvibase

import com.example.mvibase.stores.BaseStore
import com.example.mvibase.stores.DISPATCHER_TYPE
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject

internal class Dispatcher<A : IAction, R>(private val store: BaseStore<A, R>) {

    private val disposable by lazy { CompositeDisposable() }

    private val ioDispatcher by lazy {
        PublishSubject.create<A>().apply {
            store.subscribeDispatcher(this@apply,
                DISPATCHER_TYPE.IO
            ).addTo(disposable)
        }
    }

    private val uiDispatcher by lazy {
        PublishSubject.create<A>().apply {
            store.subscribeDispatcher(this@apply,
                DISPATCHER_TYPE.UI
            ).addTo(disposable)
        }
    }

    private val computeDispatcher by lazy {
        PublishSubject.create<A>().apply {
            store.subscribeDispatcher(this@apply,
                DISPATCHER_TYPE.COMPUTE
            ).addTo(disposable)
        }
    }

    fun dispatch(action: A) {
        when (action) {
            is IOAction -> ioDispatcher.onNext(action)
            is UIAction -> uiDispatcher.onNext(action)
            is ComputeAction -> computeDispatcher.onNext(action)
            else -> throw IllegalArgumentException("Wrong action type : ${action.javaClass}")
        }
    }

    fun clear() {
        disposable.clear()
    }

}