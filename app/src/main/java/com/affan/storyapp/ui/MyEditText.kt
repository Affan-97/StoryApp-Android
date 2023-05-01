package com.affan.storyapp.ui

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import com.affan.storyapp.R


class MyEditText : AppCompatEditText {
    private var errorTextView: TextView? = null


    constructor(context: Context) : super(context) {
        init()
    }


    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init()
    }


    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val errorMessage =
                    if ((s?.length
                            ?: 0) < 8
                    ) resources.getString(R.string.invalid_password) else null
                setError(errorMessage)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setError(errorMessage: String?) {
        if (errorTextView == null) {
            errorTextView = TextView(context)
            errorTextView?.setTextColor(Color.RED)
            errorTextView?.gravity = Gravity.START


            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.topMargin = 8
            layoutParams.gravity = Gravity.END
            layoutParams.leftMargin = 32
            val parent = parent as LinearLayout
            val index = parent.indexOfChild(this)
            parent.addView(errorTextView, index + 1, layoutParams)
        }
        errorTextView?.text = errorMessage
        errorTextView?.visibility =
            if (errorMessage != null) TextView.VISIBLE else TextView.GONE
    }


}