package com.mab.mmhomework.network.wrappers

import com.mab.mmhomework.network.DataWrapper
import com.mab.mojoapp.network.HError
import retrofit2.Response

/**
 * @author MAB
 */
class ApiCallback {

    companion object {
        suspend fun <T> getResponse(request: suspend () -> Response<T>): DataWrapper<T> {
            val response = request.invoke()

            val body: T? = response.body()
            if (response.isSuccessful) {
                body?.let {
                    return DataWrapper.success(body)
                }
                return HError.getDefaultErrorWrapper()
            } else {
                return HError.getObjectWithJsonError(response.errorBody()?.string())
            }
        }
    }

}