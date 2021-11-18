package com.mab.mojoapp.storage

import android.provider.MediaStore
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.mab.mojoapp.network.entities.Member
import com.mab.mojoapp.network.entities.Members
import com.mab.mojoapp.utils.UPersistence

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

    fun addMember(member: Member) {
        var members = Members()
        if (haveInitialized()) {
            members = getAll()
        }
        members.add(member)
        store(members)
    }

    fun remove(member: Member) {
        val members = getAll()
        for (m in members) {
            if (m.isSame(member)) {
                members.remove(m)
                break
            }
        }
        store(members)
    }

    fun moveUp(member: Member) {
        val members = getAll()
        var m: Member
        for (i in 0..members.size) {
            m = members[i]
            if (m.isSame(member)) {
                if (i - 1 >= 0) {
                    members.remove(m)
                    members.add(i - 1, m)
                }
                break
            }
        }
        store(members)
    }

    fun moveDown(member: Member) {
        val members = getAll()
        var m: Member
        for (i in 0..members.size) {
            m = members[i]
            if (m.isSame(member)) {
                if (i + 1 < members.size) {
                    members.remove(m)
                    members.add(i + 1, m)
                }
                break
            }
        }
        store(members)
    }


}