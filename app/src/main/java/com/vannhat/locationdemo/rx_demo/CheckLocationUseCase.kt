package com.vannhat.locationdemo.rx_demo

import android.annotation.SuppressLint
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import io.reactivex.Single

/**
 * Created by VanNhat on 23/07/2019.
 */
class CheckLocationUseCase : UseCase<CheckLocationUseCase.Input, LocationSettingsResponse>() {

    override fun getSingleTaskCase(input: Input): Single<LocationSettingsResponse> {
        return Single.create { emitter ->
            input.settingsClient.checkLocationSettings(
                getLocationSettingRequest())
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun getLocationSettingRequest(): LocationSettingsRequest {
        val locationRequest = LocationRequest()
            .setInterval(60000) // cycle time for update (Millisecond)
            .setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        return LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
    }

    data class Input(val settingsClient: SettingsClient) : UseCase.Input()

}
