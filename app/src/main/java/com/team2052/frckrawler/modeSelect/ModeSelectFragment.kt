package com.team2052.frckrawler.modeSelect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.team2052.frckrawler.databinding.ModeSelectFragmentBinding
import com.team2052.frckrawler.databinding.SampleFragmentBinding
import com.team2052.frckrawler.sample.SampleScreenState
import com.team2052.frckrawler.sample.SampleViewModel
import com.team2052.frckrawler.views.ExpandableCard
import java.util.zip.Inflater

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
//                val remoteScoutCard = ExpandableCard(requireContext());
//                remoteScoutCard.setHeaderInfo("Remote Scout", "I want to connect to a server and scout")
//                val remoteScoutDropdown = ArrayList<String>()
//                remoteScoutDropdown.add("soemthing")
//                remoteScoutDropdown.add("cool")
//                remoteScoutDropdown.add("awesome")
//                remoteScoutCard.addDropdown("Server", remoteScoutDropdown)
//                binding!!.root.addView(remoteScoutCard)
//
//                val serverCard = ExpandableCard(requireContext());
//                serverCard.setHeaderInfo("Server", "This device will be a server for remote scouts")
//                val serverDropdown = ArrayList<String>()
//                serverDropdown.add("soemthing")
//                serverDropdown.add("cool")
//                serverDropdown.add("awesome")
//                serverCard.addDropdown("something", serverDropdown)
//                binding!!.root.addView(serverCard)
//
//                val scoutCard = ExpandableCard(requireContext());
//                scoutCard.setHeaderInfo("Solo Scouting", "I want to scout without connecting to other devices")
//                val scoutDropdown = ArrayList<String>()
//                scoutDropdown.add("soemthing")
//                scoutDropdown.add("cool")
//                scoutDropdown.add("awesome")
//                scoutCard.addDropdown("cool", scoutDropdown)
//                binding!!.root.addView(scoutCard)
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