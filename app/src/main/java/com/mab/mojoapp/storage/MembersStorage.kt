package com.mab.mojoapp.storage

import android.provider.MediaStore
import com.mab.mojoapp.network.entities.Members
import com.mab.mojoapp.utils.UPersistence
import java.lang.reflect.Member

/**
 * @author MAB
 */
object MembersStorage {

    fun haveInitialized() : Boolean = UPersistence.app.getBoolean("initialRetrieval", false)

}