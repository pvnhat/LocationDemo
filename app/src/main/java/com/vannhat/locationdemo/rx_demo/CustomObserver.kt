package com.vannhat.locationdemo.rx_demo

import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * Created by VanNhat on 19/07/2019.
 */
abstract class CustomObserver {

    internal fun onSubscribeConsumer(): Consumer<Any> {
        return Consumer { onSubscribe() }
    }

    internal fun onErrorConsumer(): Consumer<in Throwable> {
        return Consumer { onError(it) }
    }

    internal fun doFinallyAction(): Action {
        return Action { doFinally() }
    }

    internal fun onAfterTerminateAction(): Action {
        return Action { onAfterTerminate() }
    }

    open fun onSubscribe() {}

    open fun onError(throwable: Throwable) {}

    open fun doFinally() {}

    open fun onAfterTerminate() {}
}
