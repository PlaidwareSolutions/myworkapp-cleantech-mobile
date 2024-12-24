package com.example.rfidapp.util

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room.databaseBuilder
import androidx.viewbinding.ViewBinding
import com.example.rfidapp.R
import com.example.rfidapp.ReaderClass
import com.example.rfidapp.database.InvDB
import com.example.rfidapp.util.constants.Constants

abstract class ActBase<actBinding : ViewBinding> : ReaderClass() {

    lateinit var binding: actBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setViewBinding()
        setContentView(binding.root)
        bindObjects()
        initSound()
        bindListeners()
        bindMethods()
        onCreateD(savedInstanceState)
    }

    abstract fun setViewBinding(): actBinding
    abstract fun bindObjects()
    abstract fun bindListeners()
    abstract fun bindMethods()
    open fun onCreateD(savedInstanceState: Bundle?) {}

    fun hideKeybaord(view: View) {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            view.windowToken,
            0
        )
    }

    fun highlightToast(str: String?, i: Int) {
        val i2: Int
        if (i == 1) {
            playSound(1)
            i2 = R.drawable.toast_blue_style
        } else {
            playSound(2)
            i2 = R.drawable.toast_red_style
        }
        val inflate = LayoutInflater.from(this)
            .inflate(R.layout.custom_toast, findViewById<View>(R.id.ll_custom_toast) as ViewGroup?)
        (inflate.findViewById<View>(R.id.ll_custom_toast) as LinearLayout).setBackgroundResource(i2)
        (inflate.findViewById<View>(R.id.toast_text) as TextView).text =
            str
        val toast = Toast(this)
        toast.setGravity(17, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = inflate
        Handler().postDelayed({ toast.show() }, 100)
        Handler().postDelayed({ toast.cancel() }, 2000)
    }
}