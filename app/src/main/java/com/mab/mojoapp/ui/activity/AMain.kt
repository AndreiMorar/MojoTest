package com.mab.mojoapp.ui.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.Observer
import com.mab.mmhomework.network.TStatus
import com.mab.mojoapp.R
import com.mab.mojoapp.databinding.AMainBinding
import com.mab.mojoapp.extensions.afterTextChanged
import com.mab.mojoapp.network.entities.Member
import com.mab.mojoapp.network.entities.Members
import com.mab.mojoapp.storage.MembersStorage
import com.mab.mojoapp.ui.customviews.IObservableScrollView
import com.mab.mojoapp.utils.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.a_main.*
import kotlinx.android.synthetic.main.item_member.view.*


class AMain : AppCompatActivity() {

    private lateinit var _binding: AMainBinding

    private val _viewModel: AMainViewModel by viewModels()

    private lateinit var draggableHighlight: View
    private var highlightPos = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        draggableHighlight =
            LayoutInflater.from(this).inflate(R.layout.draggable_highlighter, vScrollView, false)

        _binding = AMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.tvRetry.setOnClickListener {
            loadData()
        }

        loadData()

        setupForm()

        setupDraggable()

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
        with(_binding.etName) {
            requestFocus()
            showKeyboard(this)
        }
    }

    fun showKeyboard(et: EditText) {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    fun hideForm() {
        _binding.vForm.visibility = View.GONE
        clearForm()
        hideKeyboard()
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
            persistMember()
        }
        _binding.etName.afterTextChanged { validateForm() }
        _binding.etPosition.afterTextChanged { validateForm() }
        _binding.etLocation.afterTextChanged { validateForm() }
    }

    fun validateForm() {
        _binding.tvAdd.isClickable =
            etName.text.isNotEmpty() && etPosition.text.isNotEmpty() && etLocation.text.isNotEmpty()
    }

    fun persistMember() {
        val member = Member(
            etName.text.toString(),
            etPosition.text.toString(),
            etLocation.text.toString(),
            ""
        )
        MembersStorage.addMember(member)
        addMember(member)
        hideForm()
        scrollViewToBottom()
        checkListEmptiness()
        tvRetry.visibility = View.GONE
    }

    fun populateUI(items: Members) {
        _binding.vList.removeAllViews() //just to be safe
        for (item in items) {
            addMember(item)
        }
        checkListEmptiness()
    }

    fun addMember(item: Member) {
        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.item_member, _binding.vList, false).apply {
            tvNameAndPosition.text =
                getString(R.string.item_name_and_position, item.name, item.position)
            tvLocation.text = getString(R.string.item_location, item.location)
            if (item.pic.isNotEmpty()) {
                Picasso.get()
                    .load(item.pic)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(ivAvatar)
            } else {
                ivAvatar.setImageResource(R.drawable.ic_placeholder)
            }

            tag = item
            ivRemove.setOnClickListener {
                removeMember(it.parent as View)
            }
            tvMoveUp.setOnClickListener {
                moveUpMember((it.parent as View).parent as View)
            }
            tvMoveDown.setOnClickListener {
                moveDownMember((it.parent as View).parent as View)
            }

            setOnLongClickListener {
                vScrollView.isInDragMode(true)
                startDraggingView(it)
                true
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
        } else {
            _binding.tvStatus.text = ""
        }
    }

    fun moveUpMember(view: View) {
        val member: Member = view.tag as Member
        MembersStorage.moveUp(member)
        val pos = vList.indexOfChild(view)
        if (pos - 1 >= 0) {
            vList.removeView(view)
            vList.addView(view, pos - 1)
        }
    }

    fun moveDownMember(view: View) {
        val member: Member = view.tag as Member
        MembersStorage.moveDown(member)
        val pos = vList.indexOfChild(view)
        if (pos + 1 < vList.childCount) {
            vList.removeView(view)
            vList.addView(view, pos + 1)
        }
    }

    fun scrollViewToBottom() {
        vScrollView.post(Runnable { vScrollView.fullScroll(ScrollView.FOCUS_DOWN) })
    }

    fun startDraggingView(view: View) {
        vList.removeView(view)
        vDraggable.addView(view)
    }

    fun setupDraggable() {
        vScrollView.setListener(object : IObservableScrollView {
            override fun onScrolledY(y: Float, scrollY: Int) {
                val position = getPosition(y, scrollY)

                if (highlightPos != position) {
                    vList.removeView(draggableHighlight)
                    highlightPos = position
                    println("LIST ITEMS before: ${vList.childCount}")
                    vList.addView(draggableHighlight, position)
                    println("LIST ITEMS after: ${vList.childCount}")
                }

                vDraggable.y = y
            }

            override fun onDragEnded(y: Float, scrollY: Int) {
                println("SCROLL Y :: $scrollY")
                vList.removeView(draggableHighlight)
                val view = vDraggable.get(0)
                vDraggable.removeAllViews()
                val position = getPosition(y, scrollY)
                vList.addView(view, position)

            }
        })
    }

    fun getPosition(y: Float, scrollY: Int):Int{
        var yOnSCreenPos: Int = y.toInt()
        val itemH = vDraggable.height
        val scrollViewH = vScrollView.height

        if (yOnSCreenPos <= 0) yOnSCreenPos = 0
        if (yOnSCreenPos >= scrollViewH) yOnSCreenPos = scrollViewH

        val percentYScrollView = yOnSCreenPos.toFloat() / scrollViewH.toFloat()
        var posInScrollViewY = scrollViewH * percentYScrollView

        posInScrollViewY += scrollY

        val position = Math.round(posInScrollViewY / itemH)
        return position
    }

}