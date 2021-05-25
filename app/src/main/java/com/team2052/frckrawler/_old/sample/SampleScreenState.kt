package com.team2052.frckrawler._old.sample

sealed class SampleScreenState {
  object Loading: SampleScreenState()
  object Error: SampleScreenState()

  data class Content(
    val teamName: String
  ): SampleScreenState()
}