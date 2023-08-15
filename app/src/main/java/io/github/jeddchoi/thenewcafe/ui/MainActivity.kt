package io.github.jeddchoi.thenewcafe.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.jeddchoi.authentication.navigateToAuth
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
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private lateinit var navController: NavHostController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("✅")
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
                    navController = rememberNavController()

                    RootScreen(
                        networkMonitor = networkMonitor,
                        modifier = maxSizeModifier,
                        navController = navController
                    )

                    val rootViewModel: RootViewModel = hiltViewModel()
                    val redirectToAuth by rootViewModel.redirectToAuth.collectAsStateWithLifecycle()
                    val shouldRunService by rootViewModel.shouldRunService.collectAsStateWithLifecycle()
                    val navigateToStoreDetail by rootViewModel.navigateToStoreDetail.collectAsStateWithLifecycle()
                    rootViewModel.arriveOnSeat.collectAsStateWithLifecycle()

                    LaunchedEffect(shouldRunService) {
                        if (shouldRunService) {
                            Intent(applicationContext, SessionService::class.java).also {
                                it.action = SessionService.Action.START.name
                                startForegroundService(it)
                            }
                        }
                    }

                    LaunchedEffect(redirectToAuth) {
                        if (redirectToAuth) {
                            navController.navigateToAuth()
                        }
                    }


                    DisposableEffect(Unit) {
                        val listener = Consumer<Intent> { intent ->
                            Timber.i("onNewIntent : $intent ${intent.dataString}")
                            if (intent.action == Intent.ACTION_VIEW && intent.dataString != null) {
                                Timber.i("onNewIntent -> handleDeepLink $intent")
                                navController.handleDeepLink(intent)
                            }
                            if (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
                                Timber.i("onNewIntent -> ACTION_NDEF_DISCOVERED $intent")
                                performNfcRead(intent) {
                                    rootViewModel.taggedNfc(it)
                                }
                            }
                        }
                        addOnNewIntentListener(listener)
                        onDispose { removeOnNewIntentListener(listener) }
                    }


                    LaunchedEffect(navigateToStoreDetail) {
                        navigateToStoreDetail?.let {
                            handleDeepLink(it)
                        }
                    }
                }
            }
        }
        viewModel.initialize()
    }

//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//
//
//    }

    private fun performNfcRead(intent: Intent, onRead: (Uri) -> Unit) {
        Timber.v("✅")
        val messages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES,
                NdefMessage::class.java
            )?.toList()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                ?.map { it as NdefMessage }?.toList()
        }

        messages?.first()?.records?.first()?.toUri()?.let {
            onRead(it)
//            handleDeepLink(it)
        }
    }

    private fun handleDeepLink(uri: Uri): Boolean {
        Timber.v("✅")
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            uri,
            this,
            MainActivity::class.java
        )
        deepLinkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        intent = deepLinkIntent
        return navController.handleDeepLink(deepLinkIntent)
    }
}


fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}
