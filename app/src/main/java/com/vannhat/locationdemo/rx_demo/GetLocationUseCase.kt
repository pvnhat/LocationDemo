package com.vannhat.locationdemo.rx_demo

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import io.reactivex.Single

/**
 * Created by VanNhat on 22/07/2019.
 */

class GetLocationUseCase : UseCase<GetLocationUseCase.Input, Location>() {

    @SuppressLint("MissingPermission")
    override fun getSingleTaskCase(input: GetLocationUseCase.Input): Single<Location> {
        return Single.create { emitter ->
            input.fusedLocationProviderClient.lastLocation?.addOnCompleteListener {
                emitter.onSuccess(it.result)
            }

            input.fusedLocationProviderClient.lastLocation?.addOnFailureListener {
                emitter.onError(it)
            }
        }
    }


    data class Input(val fusedLocationProviderClient: FusedLocationProviderClient) : UseCase.Input()
}
