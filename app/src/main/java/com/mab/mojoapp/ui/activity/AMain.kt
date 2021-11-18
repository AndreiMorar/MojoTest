package com.mab.mojoapp.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mab.mojoapp.databinding.AMainBinding

class AMain : AppCompatActivity() {

    private lateinit var _binding: AMainBinding

    private val _viewModel: AMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = AMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _viewModel.membersLiveData.observe(this@AMain, Observer { resp ->
            println("RESP :: ${resp.status} | ${resp.data}")
        })

        _viewModel.getMembers()


    }

}