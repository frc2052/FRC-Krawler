package com.team2052.frckrawler.ui.nav

enum class NavScreen(val route: String, val title: String) {
    STARTUP("startup_screen", "startup"),

    MODE_SELECT("mode_select_screen", "mode select"),

    SCOUT("scout_screen", "scout"),
    SCOUT_HOME("scout_home_screen", "home"),
    SCOUT_MATCHES("scout_matches_screen", "matches"),

    SERVER("server_screen", "server"),
    SERVER_HOME("server_home_screen", "home"),
    SERVER_MATCHES("server_matches_screen", "matches"),
    SERVER_SEASONS("server_seasons_screen", "seasons"),

    METRICS("metrics_screen", "metrics"),
    MATCH_METRICS("match_metrics_screen", "match metrics"),
    PIT_METRICS("pit_metrics_screen", "pit metrics");

    override fun toString() = route
}