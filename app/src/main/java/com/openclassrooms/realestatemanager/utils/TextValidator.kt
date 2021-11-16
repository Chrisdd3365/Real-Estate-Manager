package com.openclassrooms.realestatemanager.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

abstract class TextValidator(private val editText: EditText?) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        validate(editText, editText?.text?.toString())
    }

    override fun afterTextChanged(s: Editable?) {}

    abstract fun validate(editText: EditText?, text: String?)
}