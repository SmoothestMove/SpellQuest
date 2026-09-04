package com.example.ui.components

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsHelper(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Throwable) {
            Log.e("TtsHelper", "Failed to construct TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        try {
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w("TtsHelper", "Language US not supported or missing data")
                    // Still mark ready so it doesn't crash on default fallback
                    isReady = true
                } else {
                    tts?.setSpeechRate(0.85f) // Slightly slower, clear rate for elementary learners
                    tts?.setPitch(1.1f) // Cheerful, kid-friendly pitch
                    isReady = true
                }
            } else {
                Log.e("TtsHelper", "TTS Initialization failed with status: $status")
            }
        } catch (e: Throwable) {
            Log.e("TtsHelper", "Error in onInit", e)
        }
    }

    fun speakWord(word: String) {
        try {
            if (!isReady || tts == null) return
            tts?.stop()
            tts?.setSpeechRate(0.85f)
            tts?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "WORD_$word")
        } catch (e: Throwable) {
            Log.e("TtsHelper", "speakWord error", e)
        }
    }

    fun speakSlowWord(word: String) {
        try {
            if (!isReady || tts == null) return
            tts?.stop()
            tts?.setSpeechRate(0.55f)
            tts?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "SLOW_WORD_$word")
        } catch (e: Throwable) {
            Log.e("TtsHelper", "speakSlowWord error", e)
        }
    }

    fun speakCustom(text: String) {
        try {
            if (!isReady || tts == null || text.isBlank()) return
            tts?.stop()
            tts?.setSpeechRate(0.85f)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "CUSTOM_TEXT")
        } catch (e: Throwable) {
            Log.e("TtsHelper", "speakCustom error", e)
        }
    }

    fun speakSentence(sentence: String) {
        try {
            if (!isReady || tts == null) return
            tts?.stop()
            // Replace underline blanks with "blank" so TTS reads it nicely
            val readable = sentence.replace(Regex("_+"), "blank")
            tts?.speak(readable, TextToSpeech.QUEUE_FLUSH, null, "SENTENCE")
        } catch (e: Throwable) {
            Log.e("TtsHelper", "speakSentence error", e)
        }
    }

    fun spellOutLoud(word: String) {
        try {
            if (!isReady || tts == null) return
            tts?.stop()
            val spelled = word.map { it.uppercaseChar() }.joinToString(separator = ". ")
            tts?.speak("$word. $spelled. $word", TextToSpeech.QUEUE_FLUSH, null, "SPELL_OUT")
        } catch (e: Throwable) {
            Log.e("TtsHelper", "spellOutLoud error", e)
        }
    }

    fun speakEncouragement() {
        try {
            val cheers = listOf(
                "Awesome job!",
                "You got it right!",
                "Super speller!",
                "Spot on!",
                "Way to go, superstar!",
                "Brilliant spelling!",
                "Fantastic work!"
            )
            val pick = cheers.random()
            tts?.speak(pick, TextToSpeech.QUEUE_ADD, null, "CHEER")
        } catch (e: Throwable) {
            Log.e("TtsHelper", "speakEncouragement error", e)
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Throwable) {
            Log.e("TtsHelper", "shutdown error", e)
        } finally {
            tts = null
            isReady = false
        }
    }
}
