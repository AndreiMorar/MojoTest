package com.mab.mojoapp.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ScrollView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.Observer
import com.mab.mmhomework.network.TStatus
import com.mab.mojoapp.R
import com.mab.mojoapp.databinding.AMainBinding
import com.mab.mojoapp.extensions.afterTextChanged
import com.mab.mojoapp.network.entities.Member
import com.mab.mojoapp.network.entities.Members
import com.mab.mojoapp.storage.MembersStorage
import com.mab.mojoapp.utils.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.a_main.*
import kotlinx.android.synthetic.main.item_member.view.*


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

        setupForm();

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
        _binding.tvStatus.setText(R.string.status_error)
        _binding.tvRetry.visibility = View.VISIBLE
    }

    fun showForm() {
        clearForm()
        _binding.vForm.visibility = View.VISIBLE
    }

    fun hideForm() {
        _binding.vForm.visibility = View.GONE
        clearForm()
    }

    fun clearForm() {
        _binding.etName.setText("")
        _binding.etPosition.setText("")
        _binding.etLocation.setText("")
    }

    fun setupForm() {
        _binding.tvAddMember.setOnClickListener {
            showForm()
        }
        _binding.tvNevermind.setOnClickListener {
            hideForm()
        }
        _binding.tvAdd.setOnClickListener {
            addMemberFromForm()
        }
        _binding.etName.afterTextChanged { validateForm() }
        _binding.etPosition.afterTextChanged { validateForm() }
        _binding.etLocation.afterTextChanged { validateForm() }
    }

    fun validateForm() {
        _binding.tvAdd.isClickable =
            etName.text.isNotEmpty() && etPosition.text.isNotEmpty() && etLocation.text.isNotEmpty()
    }

    fun addMemberFromForm() {
        val member = Member(
            etName.text.toString(),
            etPosition.text.toString(),
            etLocation.text.toString(),
            "https://ptitchevreuil.github.io/mojo/francescu.jpg"
        )
        MembersStorage.addMember(member)
        addMember(member)
        hideForm()
        vScrollView.post(Runnable { vScrollView.fullScroll(ScrollView.FOCUS_DOWN) })
        checkListEmptiness()
    }

    fun populateUI(items: Members) {
        _binding.vList.removeAllViews() //just to be safe
        for (item in items) {
            addMember(item)
        }
    }

    fun addMember(item: Member) {
        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.item_member, _binding.vList, false).apply {
            tvNameAndPosition.text =
                getString(R.string.item_name_and_position, item.name, item.position)
            tvLocation.text = getString(R.string.item_location, item.location)
            if (item.pic != null && item.pic.isNotEmpty()) {
                Picasso.get()
                    .load(item.pic)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(ivAvatar)
            }

            tag = item
            ivRemove.setOnClickListener {
                removeMember(it.parent as View)
            }

            _binding.vList.addView(this)
        }
    }

    fun removeMember(view: View) {
        val member: Member = view.tag as Member
        MembersStorage.remove(member)
        _binding.vList.removeView(view)
        checkListEmptiness()
    }

    fun checkListEmptiness() {
        if (_binding.vList.childCount == 0) {
            _binding.tvStatus.setText(R.string.nothing_to_show)
        }else{
            _binding.tvStatus.text = ""
        }
    }

}