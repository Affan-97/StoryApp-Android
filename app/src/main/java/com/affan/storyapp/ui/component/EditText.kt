package com.affan.storyapp.ui.component

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class EditText :AppCompatEditText {
    constructor(context: Context) : super(context) {
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun onTextChanged(
        text: CharSequence,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if(text.length<8){
            setError("Les than 8")
        }else{
            setError(null)
        }

    }
}