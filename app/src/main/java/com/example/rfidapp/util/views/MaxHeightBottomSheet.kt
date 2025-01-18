package com.example.rfidapp.util.views

import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class MaxHeightBottomSheet(contentLayoutId: Int) : BottomSheetDialogFragment(contentLayoutId) {

    private var keypadOpenStatus = false
    private var isMaxHeight = false

    constructor(
        contentLayoutId: Int,
        isMaxHeight: Boolean
    ) : this(contentLayoutId = contentLayoutId) {
        this.isMaxHeight = isMaxHeight
    }

    override fun onStart() {
        super.onStart()
        if (isMaxHeight.not()) {
            setMaxHeight(.8f)
            observeKeyPad()
        }
    }

    private fun setMaxHeight(percent: Float) {
        dialog?.window?.setGravity(Gravity.BOTTOM)
        val windowHeight = requireActivity().resources.displayMetrics.heightPixels
        val maxAllowedHeight = if (percent == 1f) ViewGroup.LayoutParams.MATCH_PARENT
        else (windowHeight * percent).toInt()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, maxAllowedHeight)
    }

    private fun observeKeyPad() {
        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.window?.decorView?.viewTreeObserver?.addOnGlobalLayoutListener {
            val windowHeight = requireActivity().resources.displayMetrics.heightPixels
            val rect = Rect()
            dialog?.window?.decorView?.getWindowVisibleDisplayFrame(rect)
            val keypadHeight = windowHeight - rect.bottom
            val isKeypadOpen = keypadHeight > 0

            if (isKeypadOpen && !keypadOpenStatus) {
                keypadOpenStatus = true
                setMaxHeight(1f)
            } else if (!isKeypadOpen && keypadOpenStatus) {
                keypadOpenStatus = false
                setMaxHeight(.8f)
            }
        }
    }

}
