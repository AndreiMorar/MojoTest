
package com.mab.mojoapp.network

import com.google.gson.Gson
import com.mab.mmhomework.network.DataWrapper
import com.mab.mojoapp.network.entities.ResponseError

object HError {

    fun <T> getObjectWithJsonError(errorBody: String?): DataWrapper<T> {
        var errResp: ResponseError? = null
        try {
            errResp = Gson().fromJson(errorBody, ResponseError::class.java)
        } catch (e: Exception) {
        }
        return DataWrapper.error(errResp?.error ?: getDefaultErrorObj())
    }

    fun <T> getDefaultErrorWrapper(): DataWrapper<T> {
        return DataWrapper.error(getDefaultErrorObj())
    }

    fun getDefaultErrorObj(): ServerError {
        return ServerError().apply {
            message = "Oups. Some generic error !!"
        }
    }

}