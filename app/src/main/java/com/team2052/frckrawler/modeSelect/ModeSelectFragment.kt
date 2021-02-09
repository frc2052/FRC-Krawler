package com.team2052.frckrawler.modeSelect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.databinding.ModeSelectFragmentBinding

class ModeSelectFragment : Fragment() {

    private lateinit var viewModel: ModeSelectViewModel
    private var binding: ModeSelectFragmentBinding? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = ModeSelectFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ModeSelectViewModel::class.java)

        // Whenever we get a new SampleScreenState, call our render() function
        viewModel.state.observe(viewLifecycleOwner, ::render)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    // Render a new screen state
    private fun render(state: ModeSelectScreenState) {
        when (state) {
            is ModeSelectScreenState.Loading -> {

            }

            is ModeSelectScreenState.Error -> {
                setSampleText("Error")
            }

            is ModeSelectScreenState.Content -> {
                val list = ArrayList<String>();
                list.add("custom");
                list.add("passed");
                list.add("list");

                binding?.apply {
                    remoteScoutExpandable.addSpinner("server", list)
                    serverExpandable.addSpinner("server", list)
                    serverExpandable.setContinueButtonListener{
                        findNavController().navigate(R.id.action_modeSelectFragment_to_serverFragment)
                    }
                    soloScoutingExpandable.addSpinner("server", list)
                }
            }
        }
    }

    private fun setSampleText(text: String) {
        binding?.apply {
            text
        }
    }

}