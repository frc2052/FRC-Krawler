package com.team2052.frckrawler.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.team2052.frckrawler.R
import com.team2052.frckrawler.databinding.LabeledSpinnerBinding

class LabeledSpinner : ConstraintLayout {

    private val binding: LabeledSpinnerBinding = LabeledSpinnerBinding.inflate(LayoutInflater.from(context), this)

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs) {

        context.withStyledAttributes(attrs, R.styleable.LabeledSpinner) {
            binding.spinnerLabel.text = this.getString(R.styleable.LabeledSpinner_spinnerLabel)
        }

    }

    fun setLabel(label: String) {
        binding.spinnerLabel.text = label
    }

    fun setOptions(options: List<String?>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
    }
}