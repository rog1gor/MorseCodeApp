package com.example.morsecode;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextValidator implements TextWatcher {

    private final EditText editText;

    public TextValidator(EditText text) {
        editText = text;
    }

    public void validate(String text) {
        Pattern p = Pattern.compile("^[\\w*]$");
        Matcher m = p.matcher(text);

    }

    @Override
    final public void afterTextChanged(Editable s) {
        String text = editText.getText().toString();
        validate(text);
    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Don't care */ }

    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) { /* Don't care */ }



}
