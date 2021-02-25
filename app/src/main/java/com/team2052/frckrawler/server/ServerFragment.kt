package com.team2052.frckrawler.server

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2052.frckrawler.databinding.ServerFragmentBinding

class ServerFragment: Fragment() {

    private var binding: ServerFragmentBinding? = null

    private var  serverAdapter = ServerAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View {
        binding = ServerFragmentBinding.inflate(inflater, container, false )
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.list?.layoutManager = LinearLayoutManager(context)
        binding?.list?.adapter = serverAdapter

    }

    private fun render() {
            for( i in 1..serverAdapter.getItemCount() ) {

            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}