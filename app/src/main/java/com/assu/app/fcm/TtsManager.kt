package com.assu.app.fcm

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale
import java.util.ArrayDeque

object TtsManager {
    private var tts: TextToSpeech? = null
    private var ready = false
    private val queue = ArrayDeque<String>()
    private var am: AudioManager? = null

    fun init(ctx: Context) {
        if (tts != null) return

        am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        tts = TextToSpeech(ctx.applicationContext) { status ->
            Log.w("TTS", "init status=$status")
            ready = (status == TextToSpeech.SUCCESS)

            if (ready) {
                // 1) 기본 언어 세팅
                val res = tts?.setLanguage(Locale.KOREAN)
                Log.w("TTS", "setLanguage result=$res")

                // 2) 한국어 보이스 찾아 직접 지정
                val voices = tts?.voices
                val koVoice = voices?.firstOrNull { v ->
                    (v.locale?.language?.equals("ko", true) == true) &&
                            (v.locale?.country?.equals("KR", true) == true)
                    // && v.name.contains("network", ignoreCase = true) // 있으면 네트워크 보이스 선호
                }
                if (koVoice != null) {
                    val ok = tts?.setVoice(koVoice)
                    Log.w("TTS", "setVoice=$ok, voice=${koVoice.name}")
                } else {
                    Log.w("TTS", "한국어 보이스를 찾지 못했음")
                }
            }

            // 대기열 처리
            while (ready && queue.isNotEmpty()) {
                speakNow(queue.removeFirst())
            }
        }

        tts?.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        )
    }

    fun speak(ctx: Context, text: String) {
        init(ctx)
        if (!ready) queue.addLast(text) else speakNow(text)
    }

    private fun speakNow(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, System.currentTimeMillis().toString())
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
        queue.clear()
    }
}