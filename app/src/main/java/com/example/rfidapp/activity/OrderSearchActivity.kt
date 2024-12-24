package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import com.example.rfidapp.R
import com.example.rfidapp.databinding.ActivityOrderSearchBinding
import com.example.rfidapp.util.ActBase

class OrderSearchActivity : ActBase<ActivityOrderSearchBinding>() {

    override fun setViewBinding() = ActivityOrderSearchBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {
        setSearchViewListner()
    }

    override fun bindMethods() {
        initTabs()
    }

    private fun initTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Order"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Customer"))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSearchViewListner(){
        binding.apply {
            search.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val closeIcon: Drawable? = if (!s.isNullOrEmpty()) getDrawable(R.drawable.ic_close) else null
                    closeIcon?.setBounds(0, 0, closeIcon.intrinsicWidth, closeIcon.intrinsicHeight)

                    val searchIcon: Drawable? = getDrawable(R.drawable.ic_search)
                    searchIcon?.setBounds(0, 0, searchIcon.intrinsicWidth, searchIcon.intrinsicHeight)

                    search.setCompoundDrawables(
                        searchIcon,
                        null,
                        closeIcon,
                        null
                    )
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            search.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val drawableEnd = search.compoundDrawables[2]
                    if (drawableEnd != null) {
                        val drawableWidth = drawableEnd.bounds.width()
                        val touchAreaStart = search.width - search.paddingEnd - drawableWidth
                        if (event.rawX >= touchAreaStart) {
                            search.text.clear()
                            return@setOnTouchListener true
                        }
                    }
                }
                false
            }
        }
    }
}