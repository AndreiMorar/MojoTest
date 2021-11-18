package com.mab.mmhomework.network

import com.mab.mmhomework.network.wrappers.ApiCallback
import com.mab.mojoapp.network.RetrofitBuilder
import com.mab.mojoapp.network.entities.Members
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ConfigsSSOT {

    suspend fun getMembers(): Flow<DataWrapper<Members>> {
        return flow {
            emit(DataWrapper.loading())

            val result: DataWrapper<Members> = ApiCallback.getResponse {
                RetrofitBuilder.createConfig().getAllTeamMembers()
            }

            if (result.status == TStatus.SUCCESS) {
                result.data?.let { data ->
                    storeMembers(data)
                }
            }

            emit(result)
        }.flowOn(Dispatchers.IO)
    }


    //
    // PRIVATE IMPLEMENTATION
    //

    private fun storeMembers(resp: Members) {
        //TODO:
    }

}