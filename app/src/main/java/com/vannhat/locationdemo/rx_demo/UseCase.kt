package com.vannhat.locationdemo.rx_demo

import android.annotation.SuppressLint
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by VanNhat on 22/07/2019.
 */
abstract class UseCase<in T : UseCase.Input, O> {
    @SuppressLint("CheckResult")

    fun execute(input: T, observer: SingleCustomObserver<O>) {
        getSingleTaskCase(input).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(observer.onSubscribeConsumer())
            .doAfterTerminate(observer.onAfterTerminateAction())
            .doFinally(observer.doFinallyAction())
            .subscribe(
                observer.getSuccessConsumer(),
                observer.onErrorConsumer()
            )
    }

    abstract fun getSingleTaskCase(input: T): Single<O>

    abstract class Input

    open class EmptyInput : Input() {
        companion object {
            fun instance() = EmptyInput()
        }
    }
}