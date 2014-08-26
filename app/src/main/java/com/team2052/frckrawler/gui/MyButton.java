package com.team2052.frckrawler.gui;

import android.content.Context;
import android.widget.Button;

public class MyButton extends Button {

    public MyButton(Context _context, String _text, OnClickListener _listener) {

        this(_context, _text, _listener, null);
    }

    public MyButton(Context _context, String _text, OnClickListener _listener, Object _tag) {

        super(_context);
        setText(_text);
        setOnClickListener(_listener);
        setTag(_tag);
    }
}
