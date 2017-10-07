package com.team2052.frckrawler.views

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet

class LockableFloatingActionButton : FloatingActionButton {
    var locked = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun hide() {
        if (locked)
            return
        super.hide()
    }

    override fun show() {
        if (locked)
            return
        super.show()
    }
}
