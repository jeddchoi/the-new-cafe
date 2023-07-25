package io.github.jeddchoi.thenewcafe.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.jeddchoi.data.util.NetworkMonitor
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.thenewcafe.service.SessionService
import io.github.jeddchoi.thenewcafe.splash.SplashViewModel
import io.github.jeddchoi.thenewcafe.ui.root.RootScreen
import io.github.jeddchoi.thenewcafe.ui.root.RootViewModel
import timber.log.Timber
import javax.inject.Inject


/**
 * Single activity which is main entry.
 * It should be kept simple.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()
    private lateinit var navController: NavHostController

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.v("✅")
        WindowCompat.setDecorFitsSystemWindows(window, false)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }

        val maxSizeModifier = Modifier.fillMaxSize()

        setContent {
            TheNewCafeTheme {
                Surface(
                    modifier = maxSizeModifier,
                    color = MaterialTheme.colorScheme.background
                ) {
                    val rootViewModel: RootViewModel = hiltViewModel()
                    val redirectToAuth by rootViewModel.redirectToAuth.collectAsStateWithLifecycle()
                    val startMyService by rootViewModel.startMyService.collectAsStateWithLifecycle()

                    navController = rememberNavController()

                    RootScreen(
                        windowSizeClass = calculateWindowSizeClass(this),
                        networkMonitor = networkMonitor,
                        modifier = maxSizeModifier,
                        navController = navController,
                        redirectToAuth = redirectToAuth
                    )


                    LaunchedEffect(Unit) {
                        navController.currentBackStack.collect {
                            Timber.d(it.joinToString("\n"))
                        }
                    }

                    LaunchedEffect(startMyService) {
                        if (startMyService) {
                            Intent(applicationContext, SessionService::class.java).also {
                                it.action = SessionService.Action.START.name

                                startForegroundService(it)
                            }
                        }
                    }

                }
            }
        }
        viewModel.initialize()
    }
}
