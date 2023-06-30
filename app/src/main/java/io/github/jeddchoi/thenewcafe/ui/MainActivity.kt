package io.github.jeddchoi.thenewcafe.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavDeepLinkSaveStateControl
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.thenewcafe.navigation.root.RootScreen
import io.github.jeddchoi.thenewcafe.navigation.root.rememberRootState
import io.github.jeddchoi.thenewcafe.splash.SplashViewModel


/**
 * Single activity which is main entry.
 * It should be kept simple.
 */
@OptIn(NavDeepLinkSaveStateControl::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()
    lateinit var rootNavController: NavHostController
    lateinit var mainNavController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }
        setContent {
            TheNewCafeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    rootNavController = rememberNavController()
                    mainNavController = rememberNavController()

                    RootScreen(
                        startDestination = viewModel.startDestination.value,
                        rootState = rememberRootState(navController = rootNavController),
                        mainNavController = mainNavController,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val result =
            if (rootNavController.handleDeepLink(intent)) true
            else mainNavController.handleDeepLink(intent)

        Log.e("MainActivity", "new intent = ${intent?.dataString} / resut = $result")
    }
}
