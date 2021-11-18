package com.mab.mojoapp.network

import com.google.gson.annotations.SerializedName


/**
 * @author MAB
 */
class ServerError {
    @SerializedName("message")
    var message: String = ""
        get() {
            //            if (code != -1L && TranslationsSSOT.hasKey("Error${code}")) {
            //                return TranslationsSSOT.getValueForKey("Error${code}")
            //            }
            return field
        }

    @SerializedName("code")
    var code: Long = -1

}