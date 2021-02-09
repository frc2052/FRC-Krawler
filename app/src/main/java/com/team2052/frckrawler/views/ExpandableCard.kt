package com.team2052.frckrawler.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import com.team2052.frckrawler.R
import com.team2052.frckrawler.databinding.ExpandableCardBinding

class ExpandableCard : CardView {

    private val binding: ExpandableCardBinding = ExpandableCardBinding.inflate(LayoutInflater.from(context), this)

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs) {

        binding.title.text = "Title"
        binding.description.text = "Description"
        binding.continueBtn.text = "CONTINUE"

        context.withStyledAttributes(attrs, R.styleable.ExpandableCard) {
            binding.title.text = this.getString(R.styleable.ExpandableCard_title)
            binding.description.text = this.getString(R.styleable.ExpandableCard_description)
            binding.continueBtn.text = this.getString(R.styleable.ExpandableCard_continueMessage)!!.toUpperCase()
        }

        binding.dropdownBtn.setOnClickListener {
            switchCardCollapseState()
        }

    }

    public fun setContinueButtonListener(listener: View.OnClickListener) {
        binding.continueBtn.setOnClickListener(listener)
    }

    public fun addSpinner(label: String, options: List<String>) {
        val lspinner = LabeledSpinner(context)
        lspinner.setLabel(label.plus(":"))
        lspinner.setOptions(options)
        binding.collapsible.addView(lspinner)
    }

    public fun setDropdown(name: String, options: List<String>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spinner = Spinner(context)
        spinner.adapter = adapter
        binding.collapsible.addView(spinner)
    }

    private fun switchCardCollapseState() {
        if(binding.collapsible.visibility == View.VISIBLE) { // I can't find the ternary operator :<
            binding.collapsible.visibility = View.GONE
            binding.continueBtn.visibility = View.GONE
            binding.dropdownBtn.setImageDrawable(
                    ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_baseline_keyboard_arrow_down_24,
                            context.theme
                    )
            )
        } else {
            binding.collapsible.visibility = View.VISIBLE
            binding.continueBtn.visibility = View.VISIBLE
            binding.dropdownBtn.setImageDrawable(
                    ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_baseline_keyboard_arrow_up_24,
                            context.theme
                    )
            )
        }
    }

}