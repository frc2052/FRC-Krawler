package com.team2052.frckrawler.sample

sealed class SampleScreenState {
  object Loading: SampleScreenState()
  object Error: SampleScreenState()

  data class Content(
    val teamName: String
  ): SampleScreenState()
}