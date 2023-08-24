package io.github.jeddchoi.thenewcafe.nfc

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Build

fun Intent.ndefMessages() =
    if (action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES,
                NdefMessage::class.java
            )?.toList()
        } else {
            @Suppress("DEPRECATION")
            getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                ?.map { it as NdefMessage }?.toList()
        }
    } else
        null