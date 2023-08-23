package io.github.jeddchoi.thenewcafe.nfc

import android.nfc.NdefRecord
import java.nio.charset.Charset
import java.util.Arrays

fun NdefRecord.payloadText(): String? =
    if (tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(type, NdefRecord.RTD_TEXT)) {
        val payloadBytes: ByteArray = payload
        val isUTF8 =
            payloadBytes[0].toInt() and 0x080 == 0 //status byte: bit 7 indicates encoding (0 = UTF-8, 1 = UTF-16)

        val languageLength =
            payloadBytes[0].toInt() and 0x03F //status byte: bits 5..0 indicate length of language code

        val textLength = payloadBytes.size - 1 - languageLength
//        val languageCode =
//            String(payloadBytes, 1, languageLength, Charset.forName("US-ASCII"))
        String(
            payloadBytes,
            1 + languageLength,
            textLength,
            Charset.forName(if (isUTF8) "UTF-8" else "UTF-16"),
        )
    } else
        null
