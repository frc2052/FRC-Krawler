package com.team2052.frckrawler.ui

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "light",
    group = "UI Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "dark",
    group = "UI Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class FrcKrawlerPreview
