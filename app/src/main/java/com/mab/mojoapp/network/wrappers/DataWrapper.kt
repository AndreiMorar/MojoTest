package com.mab.mmhomework.network

import com.mab.mojoapp.network.ServerError

/**
 * @author MAB
 */
enum class TStatus {
    LOADING, SUCCESS, ERROR
}

class DataWrapper<T> {
    var data: T? = null
        set(data: T?) {
            isDataConsumed = false
            field = data
        }
    private var err: ServerError? = null
    var status: TStatus = TStatus.LOADING

    /** Take NOTE !!! This only works for one observer of the data. If multiple observers lister for this, only one
     * (and we don't know which one) will consume this
     */
    var isErrConsumed: Boolean = false
    var isDataConsumed: Boolean = false

    companion object {
        fun <T> success(data: T) = DataWrapper<T>().apply {
            status = TStatus.SUCCESS
            this.data = data
            isDataConsumed = false
        }

        fun <T> error(err: ServerError?) = DataWrapper<T>().apply {
            status = TStatus.ERROR
            this.err = err
            isErrConsumed = false
        }

        fun <T> loading() = DataWrapper<T>().apply {
            this.status = TStatus.LOADING
        }
    }

    fun consumeErr(): ServerError? {
        isErrConsumed = true
        return err
    }

    fun peekErr(): ServerError? {
        return err
    }

    /** Be careful when using this! You will loose the caching capability of liveData objects
     * This was created in case you don't want to re-run the logic for the success case, i.e. in splashscreen when
     * you don't need to reset the shared prefers values
     * */
    fun consumeData(): T? {
        isDataConsumed = true
        return data
    }

}