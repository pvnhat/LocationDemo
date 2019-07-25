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
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.vannhat.locationdemo.Utils.isPermissionGranted
import com.vannhat.locationdemo.Utils.requestAppPermission
import com.vannhat.locationdemo.rx_demo.CheckLocationUseCase
import com.vannhat.locationdemo.rx_demo.GetLocationUseCase
import com.vannhat.locationdemo.rx_demo.RxDemoActivity
import com.vannhat.locationdemo.rx_demo.SingleCustomObserver
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var geocoder: Geocoder? = null
    private var addresses: List<Address>? = null
    private var addressLiveData = MutableLiveData<String>()
    private var addressString = String()
    private var getLocationUseCase: GetLocationUseCase? = null
    private var checkLocationUseCase: CheckLocationUseCase? = null

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
        getLocationUseCase = GetLocationUseCase()
        checkLocationUseCase = CheckLocationUseCase()

    }

    private fun handleEvent() {
        btn_get_location.setOnClickListener {
            getLocationFromCoordinates()
        }

        btn_get_coordinates.setOnClickListener {
            getCoordinatesFromLocation()
        }

        btn_next.setOnClickListener {
            startActivity(RxDemoActivity.newInstance(this))
        }

        btn_check_location.setOnClickListener {
            checkLocation()
        }

    }

    private fun checkLocation() {
        checkLocationUseCase?.let {
            val settingClient = LocationServices.getSettingsClient(this)
            it.execute(CheckLocationUseCase.Input(settingClient),
                object : SingleCustomObserver<LocationSettingsResponse>() {
                    override fun onSubscribe() {
                        super.onSubscribe()
                        tv_location.text = getString(R.string.loading)
                    }

                    override fun onError(throwable: Throwable) {
                        super.onError(throwable)
                        if (throwable is ApiException &&
                            throwable.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            val resolvable = throwable as ResolvableApiException
                            resolvable.startResolutionForResult(this@MainActivity,
                                REQUEST_CHECK_SETTING)
                        }
                    }
                })
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
    private fun getLocationFromCoordinates() {
        if (isPermissionGranted(this) && fusedLocationProviderClient != null) {
            getLocationUseCase?.execute(GetLocationUseCase.Input(fusedLocationProviderClient!!),
                object : SingleCustomObserver<Location>() {
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

        } else {
            requestAppPermission(this)
        }
    }

    companion object {
        private const val REQUEST_CHECK_SETTING = 96
    }


}
