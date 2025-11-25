package com.team2052.frckrawler.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * A [ViewModelProvider.Factory] that uses an injected map of [KClass] to [Provider] of [ViewModel]
 * to create ViewModels.
 */
@ContributesBinding(AppScope::class)
@Inject
class MetroViewModelFactory(private val vmGraphFactory: ViewModelGraph.Factory) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
    val viewModelGraph = viewModelGraph(extras)

    val provider =
      viewModelGraph.viewModelProviders[modelClass.kotlin]
        ?: throw IllegalArgumentException("Unknown model class $modelClass")

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    return modelClass.cast(provider())
  }

  fun viewModelGraph(extras: CreationExtras): ViewModelGraph =
    vmGraphFactory.createViewModelGraph(extras)
}