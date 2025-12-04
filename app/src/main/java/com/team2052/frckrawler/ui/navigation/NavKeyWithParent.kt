package com.team2052.frckrawler.ui.navigation

import androidx.navigation3.runtime.NavKey

/**
 * A NavKey that has a parent screen
 *
 * If a screen is accessibile via deeplink, it should implement this interface
 * so we can create a proper back stack for navigation
 */
interface NavKeyWithParent {
  val parent: NavKey
}