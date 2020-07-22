package com.example.mvibase.stores

import io.reactivex.Observable

interface ChildStore<A,S> {

    fun reduce(action : Observable<A>) : Observable<S>

    fun onClear() {}

}