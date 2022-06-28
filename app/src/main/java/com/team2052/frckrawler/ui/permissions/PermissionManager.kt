package com.team2052.frckrawler.ui.permissions

/**
 * Abstraction over permission APIs to allow checking permission status without direct
 * reference to a Context.
 */
interface PermissionManager {
  fun hasPermission(permission: String): Boolean
  fun hasPermissions(permissions: List<String>): Boolean
}