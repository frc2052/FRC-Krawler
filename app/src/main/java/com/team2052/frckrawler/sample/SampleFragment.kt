package com.team2052.frckrawler.sample

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team2052.frckrawler.R
import com.team2052.frckrawler.databinding.SampleFragmentBinding

class SampleFragment : Fragment() {

  private lateinit var viewModel: SampleViewModel
  private var binding: SampleFragmentBinding? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = SampleFragmentBinding.inflate(inflater, container, false)
    return binding!!.root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProvider(this).get(SampleViewModel::class.java)

    // Whenever we get a new SampleScreenState, call our render() function
    viewModel.state.observe(viewLifecycleOwner, ::render)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }

  // Render a new screen state
  private fun render(state: SampleScreenState) {
    when (state) {
      is SampleScreenState.Loading -> {
        setSampleText("loading")
      }

      is SampleScreenState.Error -> {
        setSampleText("Error")
      }

      is SampleScreenState.Content -> {
        // Kotlin is super samrt and automatically casts our state as SampleScrenState.Content
        // so we can access state.teamName here
        setSampleText(state.teamName)
      }
    }
  }

  private fun setSampleText(text: String) {
    binding?.apply {
      sampleText.text = text
    }
  }
}