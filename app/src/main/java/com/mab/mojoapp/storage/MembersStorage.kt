package com.mab.mojoapp.storage

import android.provider.MediaStore
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.mab.mojoapp.network.entities.Members
import com.mab.mojoapp.utils.UPersistence
import java.lang.reflect.Member

/**
 * @author MAB
 */
object MembersStorage {

    fun haveInitialized(): Boolean = UPersistence.app.getBoolean("initialRetrieval", false)

    fun store(members: Members) {
        UPersistence.app.putString("members", Gson().toJson(members))
        UPersistence.app.putBoolean("initialRetrieval", true)
    }

    fun getAll(): Members {
        val json = UPersistence.app.getString("members", "{}")
        try {
            return Gson().fromJson(json, Members::class.java)
        } catch (e: JsonSyntaxException) {
            UPersistence.app.removeKeyValuePair("members")
            UPersistence.app.putBoolean("initialRetrieval", false)
        }
        return Members()
    }


}