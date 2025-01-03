package com.example.rfidapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.rfidapp.R


class HintAdapter<T> : ArrayAdapter<T> {

    constructor(context: Context, resource: Int) : super(context, resource) {
    }

    constructor(context: Context, resource: Int, textViewResourceId: Int) : super(
        context,
        resource,
        textViewResourceId
    ) {
    }

    constructor(
        context: Context,
        resource: Int,
        textViewResourceId: Int,
        objects: Array<T>
    ) : super(context, resource, textViewResourceId, objects) {
    }

    constructor(context: Context, resource: Int, objects: Array<T>) : super(
        context,
        resource,
        objects
    ) {
    }

    constructor(context: Context, resource: Int, objects: ArrayList<T>) : super(
        context,
        resource,
        objects
    ) {
    }

    constructor(context: Context, resource: Int, textViewResourceId: Int, objects: List<T>) : super(
        context,
        resource,
        textViewResourceId,
        objects
    ) {
    }

    override fun getCount(): Int {
        // don't display last item. It is used as hint.
        val count = super.getCount();
        return if (count > 0) count - 1 else count;
    }
}

class HintAdapterV2<T> : ArrayAdapter<T> {

    private var layoutResource: Int = 0
    private var _mTextViewResourceId: Int = 0

    constructor(context: Context, resource: Int) : super(context, resource) {
        layoutResource = resource
    }

    constructor(context: Context, resource: Int, textViewResourceId: Int) : super(
        context,
        resource,
        textViewResourceId
    ) {
        layoutResource = resource
        _mTextViewResourceId = textViewResourceId
    }

    constructor(
        context: Context,
        resource: Int,
        textViewResourceId: Int,
        objects: Array<T>
    ) : super(context, resource, textViewResourceId, objects) {
        layoutResource = resource
        _mTextViewResourceId = textViewResourceId
    }

    constructor(context: Context, resource: Int, objects: Array<T>) : super(
        context,
        resource,
        objects
    ) {
        layoutResource = resource
    }

    constructor(context: Context, resource: Int, objects: ArrayList<T>) : super(
        context,
        resource,
        objects
    ) {
        layoutResource = resource
    }

    constructor(context: Context, resource: Int, textViewResourceId: Int, objects: List<T>) : super(
        context,
        resource,
        textViewResourceId,
        objects
    ) {
        layoutResource = resource
        _mTextViewResourceId = textViewResourceId
    }

    /*override fun getCount():Int {
        val count = super.getCount()
        return if(count > 0) count - 1 else count
    }*/

    private class ViewHolder {
        var textView: TextView? = null
    }

    private class HintViewHolder() {
        var emptyLayout: ConstraintLayout? = null
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(layoutResource, parent, false)

            viewHolder = ViewHolder()
            viewHolder.textView =
                convertView.findViewById(if (_mTextViewResourceId == 0) android.R.id.text1 else _mTextViewResourceId)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        val item = getItem(position)

        if (item != null) {
            viewHolder.textView?.text = item.toString()
        }

        return convertView!!
    }

    private var dropDownViewResource = 0
    override fun setDropDownViewResource(resource: Int) {
        super.setDropDownViewResource(resource)
        dropDownViewResource = resource
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var viewHolder: ViewHolder? = null
        var hintViewHolder: HintViewHolder? = null

        if (position == 0) {
            if (convertView == null || convertView.tag is ViewHolder) {
                val inflater = LayoutInflater.from(context)
                convertView = inflater.inflate(R.layout.blank_layout, parent, false)

                hintViewHolder = HintViewHolder()
                convertView.tag = hintViewHolder
            } else {
                hintViewHolder = convertView.tag as HintViewHolder
            }
        } else if (convertView == null || convertView.tag !is ViewHolder) {
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(
                if (dropDownViewResource == 0) android.R.layout.simple_spinner_dropdown_item else dropDownViewResource,
                parent,
                false
            )

            viewHolder = ViewHolder()
            viewHolder.textView =
                convertView.findViewById(if (_mTextViewResourceId == 0) android.R.id.text1 else _mTextViewResourceId)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        val item = getItem(position)

        if (position == 0) {
        } else if (item != null) {
            viewHolder?.textView?.text = item.toString()
        }

        return convertView!!

    }


}

class ACArrayAdapter<T>(context: Context, resource: Int, objects: List<T>) :
    ArrayAdapter<T>(context, resource, objects) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)

        if (position == count - 1) {
            textView?.background = null
        } else {
            textView?.setBackgroundResource(R.drawable.bg_divider)
        }
        return view
    }
}