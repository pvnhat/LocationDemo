package com.vannhat.locationdemo.rx_demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.example.locationdemo.R
import com.vannhat.locationdemo.Utils.createLog
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_rx_demo.*
import java.util.concurrent.Executors
import java.util.concurrent.Future

class RxDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx_demo)
        initData()
        handleEvent()
    }

    private fun initData() {

    }

    private fun handleEvent() {
        btn_just.setOnClickListener {
            Observable.just(1, 2, 3, 4).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribe(
                {
                    createLog("just $it")
                },
                {
                    createLog("error ${it.message}")
                })
        }

        btn_from.setOnClickListener {
            Single.create<String> {

            }
        }

    }

    companion object {
        fun newInstance(context: Context): Intent {
            return Intent(context, RxDemoActivity::class.java)
        }
    }
}
