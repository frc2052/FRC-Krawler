package com.team2052.frckrawler.ui.main

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.NavScreen
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.startup.StartupViewModel
import java.util.*
import kotlin.random.Random

/**
 * this composable creates a common entrypoint so automatically jumping to a starting route is easier.
 */
@Composable
fun StartupScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) = FRCKrawlerScaffold(
    modifier = modifier,
    currentScreen = NavScreen.SplashScreen,
    appBar = { },
    background = { },
) {
    val viewModel: StartupViewModel = hiltNavGraphViewModel()

    ConstraintLayout(
        modifier = Modifier.fillMaxHeight(),
        constraintSet = ConstraintSet {
            val titleGroup = createRefFor("titleGroup")
            val loadingBar = createRefFor("progressIndicator")

            constrain(titleGroup) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
            constrain(loadingBar) {
                start.linkTo(parent.start)
                top.linkTo(titleGroup.bottom)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
        }
    ) {
        Column(
            modifier = Modifier.layoutId("titleGroup"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = formatAppName(),
                style = LocalTextStyle.current.merge(MaterialTheme.typography.h1)
            )
            Text(
                text = stringResource(R.string.app_version),
                style = MaterialTheme.typography.h6
            )
        }
        CircularProgressIndicator(modifier = Modifier
            .size(48.dp)
            .layoutId("progressIndicator"))
    }

    viewModel.buffer(onComplete = {
        navController.navigate(NavScreen.ModeSelectScreen.route)
    })
}

@Composable
private fun formatAppName(@StringRes appNameResourceId: Int = R.string.app_name): String {
    val ca = stringResource(R.string.app_name).toCharArray()
    if (Locale.getDefault().language.equals("en") && Random.nextInt(0, 100) == 0) {
        ca[1] = ca[0]
        ca[0] = ca[4]
    }
    return String(ca)
}

