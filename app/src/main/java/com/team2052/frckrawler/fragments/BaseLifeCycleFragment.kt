package com.team2052.frckrawler.fragments

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle

abstract class BaseLifeCycleFragment<T : AndroidViewModel> : LifecycleFragment() {
    abstract val viewModelClass: Class<T>

    protected lateinit var viewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(viewModelClass)
    }
}