package com.mab.mojoapp.network.entities

import com.google.gson.annotations.SerializedName
import com.mab.mojoapp.network.ServerError

class ResponseError {
    @SerializedName("error")
    var error: ServerError? = null
}