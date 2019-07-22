package com.vannhat.locationdemo

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.locationdemo.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.vannhat.locationdemo.Utils.isPermissionGranted
import com.vannhat.locationdemo.Utils.requestAppPermission
import com.vannhat.locationdemo.rx_demo.RxDemoActivity
import com.vannhat.locationdemo.rx_demo.SingleCustomObserver
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var geocoder: Geocoder? = null
    private var addresses: List<Address>? = null
    private var addressLiveData = MutableLiveData<String>()
    private var addressString = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        handleEvent()
        observer()
    }

    private fun observer() {
        addressLiveData.observe(this, Observer {
            tv_location.text = it
        })
    }

    private fun initData() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this)

    }

    private fun handleEvent() {
        btn_get_location.setOnClickListener {
            getLocationFromCoordinates(object : SingleCustomObserver<Location>() {
                override fun onSubscribe() {
                    super.onSubscribe()
                    tv_location.text = getString(R.string.loading)
                }

                override fun onSuccess(t: Location) {
                    super.onSuccess(t)
                    addresses = geocoder?.getFromLocation(t.latitude, t.longitude, 1)
                    addresses?.get(0)?.let {
                        addressString = it.countryName
                        addressString += " " + it.adminArea
                        addressString += " " + it.getAddressLine(0)
                        addressLiveData.value = addressString
                    }
                }

                override fun onError(throwable: Throwable) {
                    super.onError(throwable)
                    Utils.createLog("Error last location : ${throwable.message}")
                }
            })
        }

        btn_get_coordinates.setOnClickListener {
            getCoordinatesFromLocation()
        }

        btn_next.setOnClickListener {
            startActivity(RxDemoActivity.newInstance(this))
        }

    }

    // Get coordinates (latitude, longitude) from Local details
    private fun getCoordinatesFromLocation() {
        addresses = geocoder?.getFromLocationName(
            "Lê Đình Lý, Hòa Thuận Nam, Hải Châu, Đà Nẵng 550000, Việt Nam", 1
        )
        addresses?.get(0)?.let {
            addressString = it.latitude.toString() + " , " + it.longitude.toString()
            addressLiveData.value = addressString
        }

    }

    // Get location detail from coordinates (latitude, longitude)
    @SuppressLint("MissingPermission", "CheckResult")
    private fun getLocationFromCoordinates(observer: SingleCustomObserver<Location>) {
        if (isPermissionGranted(this)) {

            Single.create<Location> { emitter ->
                fusedLocationProviderClient?.lastLocation?.addOnCompleteListener {
                    emitter.onSuccess(it.result)
                }

                fusedLocationProviderClient?.lastLocation?.addOnFailureListener {
                    emitter.onError(it)
                }

            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(observer.onSubscribeConsumer())
                .doAfterTerminate(observer.onAfterTerminateAction())
                .doFinally(observer.doFinallyAction())
                .subscribe(observer.getSuccessConsumer(),
                    observer.onErrorConsumer())

        } else {
            requestAppPermission(this)
        }
    }
}
