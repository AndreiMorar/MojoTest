package com.mab.mojoapp.ui.activity

import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mab.mmhomework.network.DataWrapper
import com.mab.mmhomework.network.SSOT
import com.mab.mojoapp.network.entities.Members
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AMainViewModel : ViewModel() {

    private val _membersLiveData: MutableLiveData<DataWrapper<Members>> =
        MutableLiveData<DataWrapper<Members>>()
    val membersLiveData: LiveData<DataWrapper<Members>> = _membersLiveData


    fun getMembers() = viewModelScope.launch {
        SSOT.configsSSOT.getMembers().collect {
            _membersLiveData.value = it
        }
    }

}