package com.team2052.frckrawler.sample

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import com.team2052.frckrawler.R
import com.team2052.frckrawler.compose.theme.FrcKrawlerTheme
import com.team2052.frckrawler.databinding.SampleFragment2Binding
import com.team2052.frckrawler.databinding.SampleFragmentBinding

class SampleFragment2 : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return ComposeView(requireContext()).apply {
      setContent {
        SampleComposable()
      }
    }
  }
}

@Composable
private fun SampleComposable() {
  FrcKrawlerTheme {
    Surface {
      Text("Hello from Jetpack Compose!")
    }
  }
}