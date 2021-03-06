package com.mab.mojoapp.network.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Members : ArrayList<Member>()

class Member(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("position")
    val position: String = "",
    @SerializedName("location")
    val location: String = "",
    @SerializedName("pic")
    val pic: String = ""
) : Serializable {
    fun isSame(member: Member): Boolean {
        return member.name.equals(name)
                && member.position.equals(position)
                && member.location.equals(location)
                && member.pic.equals(pic)
    }
}