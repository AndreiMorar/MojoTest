package com.mab.mojoapp.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mab.mmhomework.network.TStatus
import com.mab.mojoapp.R
import com.mab.mojoapp.databinding.AMainBinding
import com.mab.mojoapp.network.entities.Members
import com.mab.mojoapp.storage.MembersStorage
import com.mab.mojoapp.utils.UPersistence
import com.mab.mojoapp.utils.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.a_main.*
import kotlinx.android.synthetic.main.item_member.view.*
import java.lang.reflect.Member

class AMain : AppCompatActivity() {

    private lateinit var _binding: AMainBinding

    private val _viewModel: AMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = AMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.tvRetry.setOnClickListener {
            loadData()
        }

        loadData()

    }

    fun loadData() {
        tvStatus.text = ""
        tvRetry.visibility = View.GONE
        val haveInitialized = MembersStorage.haveInitialized()
        println("Loading from :: ${Utils.isNetworkConnected(this)}")
        if (!Utils.isNetworkConnected(this) && !haveInitialized) {
            println("Loading from :: NO NETWORK NOR LOCAL STORAGE")
            showError()
        } else
            if (haveInitialized) {
                println("Loading from :: local storage")
                populateUI(MembersStorage.getAll())
            } else {
                println("Loading from :: API")
                _viewModel.membersLiveData.observe(this@AMain, Observer { resp ->
                    when (resp.status) {
                        TStatus.LOADING -> tvStatus.setText(R.string.status_loading)
                        TStatus.SUCCESS -> {
                            tvStatus.text = ""
                            resp.data?.apply {
                                MembersStorage.store(this)
                                populateUI(this)
                            }
                        }
                        TStatus.ERROR -> showError()
                    }
                })
                _viewModel.getMembers()
            }
    }

    fun showError() {
        tvStatus.setText(R.string.status_error)
        tvRetry.visibility = View.VISIBLE
    }

    fun populateUI(items: Members) {
        _binding.vList.removeAllViews() //just to be safe
        val inflater = LayoutInflater.from(this)
        for (item in items) {
            inflater.inflate(R.layout.item_member, _binding.vList, false).apply {
                tvNameAndPosition.text =
                    getString(R.string.item_name_and_position, item.name, item.position)
                tvLocation.text = getString(R.string.item_location, item.location)
                Picasso.get()
                    .load(item.pic)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(ivAvatar)

                _binding.vList.addView(this)
            }

        }
    }

}