package com.mab.mojoapp.gobal

import android.app.Application
import com.mab.mojoapp.utils.UPersistence

/**
 * @author MAB
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        UPersistence.init(this)

    }
}