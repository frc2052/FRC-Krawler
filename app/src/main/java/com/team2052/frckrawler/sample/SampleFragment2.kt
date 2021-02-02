package com.team2052.frckrawler.sample

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team2052.frckrawler.R
import com.team2052.frckrawler.databinding.SampleFragment2Binding
import com.team2052.frckrawler.databinding.SampleFragmentBinding

class SampleFragment2 : Fragment() {

  private lateinit var viewModel: SampleViewModel
  private var binding: SampleFragment2Binding? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = SampleFragment2Binding.inflate(inflater, container, false)
    return binding!!.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }
}