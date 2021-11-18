package com.mab.mojoapp.network.entities

import com.google.gson.annotations.SerializedName

class Members : ArrayList<Member>()

class Member(
    @SerializedName("name")
    val name: String,
    @SerializedName("position")
    val position: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("pic")
    val pic: String
)