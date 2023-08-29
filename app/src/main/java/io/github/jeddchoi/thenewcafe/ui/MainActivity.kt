package io.github.jeddchoi.thenewcafe.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.jeddchoi.data.util.NetworkMonitor
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.thenewcafe.nfc.ndefMessages
import io.github.jeddchoi.thenewcafe.nfc.payloadText
import io.github.jeddchoi.thenewcafe.service.SessionService
import io.github.jeddchoi.thenewcafe.ui.root.RootScreen
import io.github.jeddchoi.thenewcafe.ui.root.RootViewModel
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import javax.inject.Inject


/**
 * Single activity which is main entry.
 * It should be kept simple.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity(), NfcAdapter.ReaderCallback {

    private val viewModel: RootViewModel by viewModels()

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private lateinit var navController: NavHostController

    private var adapter: NfcAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("✅ $intent ${intent.dataString} ${intent.action}")
        WindowCompat.setDecorFitsSystemWindows(window, false)

        installSplashScreen()

        adapter = NfcAdapter.getDefaultAdapter(this)

        val maxSizeModifier = Modifier.fillMaxSize()

        setContent {
            TheNewCafeTheme {
                Surface(
                    modifier = maxSizeModifier,
                    color = MaterialTheme.colorScheme.background
                ) {
                    navController = rememberNavController()
                    viewModel.userSessionStateFlow.collectAsStateWithLifecycle()
                    RootScreen(
                        redirectionByNfcRead = viewModel.redirectionEventByNfcRead,
                        redirectionToAuth = viewModel.redirectionToAuth,
                        networkMonitor = networkMonitor,
                        modifier = maxSizeModifier,
                        navController = navController
                    )

                    LaunchedEffect(Unit) {
                        viewModel.startSessionEvent.collectLatest { start ->
                            Timber.i("startSessionEvent : $start")
                            if (start) {
                                startForegroundService(
                                    Intent(
                                        applicationContext,
                                        SessionService::class.java
                                    ).also {
                                        it.action = SessionService.Action.START.name
                                    }
                                )
                            }
                        }
                    }

                    LaunchedEffect(Unit) {
                        viewModel.arriveOnSeatByNfcReadEvent.collectLatest {
                            Timber.i("arriveOnSeatWithNfcEvent : $it")
                            it?.invoke()
                        }
                    }

                    // Handle Implicit Deep Link
                    DisposableEffect(Unit) {
                        val listener = Consumer<Intent> { intent ->
                            Timber.i("onNewIntent : $intent ${intent.dataString} ${intent.action}")
                            if (intent.action == Intent.ACTION_VIEW && intent.dataString != null) {
                                Timber.i("onNewIntent -> handleDeepLink $intent")
                                navController.handleDeepLink(intent)
                            }
                        }
                        addOnNewIntentListener(listener)
                        onDispose { removeOnNewIntentListener(listener) }
                    }

                    // For NDEF_DISCOVERED_ACTION intent
                    LaunchedEffect(Unit) {
                        intent.ndefMessages()?.first()?.records?.first()?.let {
                            it.payloadText()?.let { payloadText ->
                                Timber.i("RECORD : $payloadText")
                                viewModel.readNfcSeatPosition(Uri.parse(payloadText))
                                intent.removeExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                            } ?: Timber.i("RECORD(not text) : ${String(it.payload)}")
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        adapter?.let {
            val options = Bundle()
            // Work around for some broken Nfc firmware implementations that poll the card too fast
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)

            // Enable ReaderMode for all types of card and disable platform sounds
            it.enableReaderMode(
                this,
                this,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V or
                        NfcAdapter.FLAG_READER_NFC_BARCODE,
//                        NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                options
            )
        }
    }

    override fun onPause() {
        super.onPause()
        adapter?.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag) {
        Timber.i("✅")
        // Read and or write to Tag here to the appropriate Tag Technology type class
        // in this example the card should be an Ndef Technology Type
        val ndef = Ndef.get(tag)

        // Check that it is an Ndef capable card
        if (ndef != null) {

            // If we want to read
            // As we did not turn on the NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
            // We can get the cached Ndef message the system read for us.

            ndef.cachedNdefMessage.records?.first()?.let {
                it.payloadText()?.let { payloadText ->
                    Timber.i("RECORD : $payloadText")
                    viewModel.readNfcSeatPosition(Uri.parse(payloadText))
                } ?: Timber.i("RECORD(not text) : ${String(it.payload)}")
            }
        }
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
