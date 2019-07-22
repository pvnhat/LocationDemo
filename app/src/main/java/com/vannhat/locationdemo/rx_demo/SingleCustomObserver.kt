package com.vannhat.locationdemo.rx_demo

import androidx.annotation.NonNull
import io.reactivex.functions.Consumer

/**
 * Created by VanNhat on 22/07/2019.
 */

abstract class SingleCustomObserver<T> : CustomObserver() {

    internal fun getSuccessConsumer(): Consumer<T> {
        return Consumer { this.onSuccess(it) }
    }

    open fun onSuccess(@NonNull t: T) {}
}
