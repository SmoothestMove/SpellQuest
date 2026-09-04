package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

object WordPhotoScanner {

    private const val TAG = "WordPhotoScanner"

    // Prefixes found in worksheet metadata lines (e.g. "Name: Alex", "Date: 10/12")
    private val HEADER_LINE_PREFIXES = listOf(
        "name:", "name :", "date:", "date :", "teacher:", "student:",
        "score:", "grade:", "class:", "unit:", "week:", "lesson:", "page:", "due:"
    )

    // Regex patterns for worksheet header titles that shouldn't be added as words
    private val TITLE_PATTERNS = listOf(
        Regex("^(weekly\\s+)?spelling(\\s+words?|\\s+list)?.*", RegexOption.IGNORE_CASE),
        Regex("^phonics\\s+(words?|list|practice).*", RegexOption.IGNORE_CASE),
        Regex("^word\\s+study.*", RegexOption.IGNORE_CASE),
        Regex("^(unit|week|lesson)\\s+[0-9]+.*", RegexOption.IGNORE_CASE)
    )

    // Meta words that should not be extracted as spelling words
    private val PURE_META_WORDS = setOf(
        "spelling", "vocab", "vocabulary", "worksheet", "phonics"
    )

    /**
     * Extracts words from a given Bitmap. Tries Gemini Vision if an API key is configured,
     * otherwise (or on failure) falls back to on-device Google ML Kit Text Recognition.
     */
    suspend fun extractWordsFromBitmap(bitmap: Bitmap): List<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        // Try Gemini Vision first if valid API key is present
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val geminiWords = extractWordsWithGemini(bitmap, apiKey)
                if (geminiWords.isNotEmpty()) {
                    return@withContext geminiWords
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini Vision extraction failed, falling back to ML Kit", e)
            }
        }

        // Fallback to fast, on-device Google ML Kit Text Recognition
        return@withContext extractWordsWithMlKit(bitmap)
    }

    /**
     * Extracts words using on-device ML Kit Text Recognition
     */
    suspend fun extractWordsWithMlKit(bitmap: Bitmap): List<String> = suspendCancellableCoroutine { continuation ->
        try {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(bitmap, 0)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val rawText = visionText.text
                    val parsed = parseSpellingWordsFromText(rawText)
                    continuation.resume(parsed)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "ML Kit text recognition error", exception)
                    continuation.resume(emptyList())
                }
        } catch (e: Exception) {
            Log.e(TAG, "ML Kit initialization error", e)
            continuation.resume(emptyList())
        }
    }

    /**
     * Cleans OCR raw text:
     * - Discards lines that are purely sheet headers (e.g. "Week 4 Spelling Words")
     * - Strips line numbering ("1.", "2)", "3-")
     * - Strips bullets ("•", "*", "-")
     * - Normalizes to valid lowercase English words
     * - Deduplicates while preserving order
     */
    fun parseSpellingWordsFromText(rawText: String): List<String> {
        val lines = rawText.split(Regex("[\n\r]+"))
        val candidateWords = mutableListOf<String>()

        for (rawLine in lines) {
            val trimmedLine = rawLine.trim()
            if (trimmedLine.isBlank()) continue

            val lowerLine = trimmedLine.lowercase()

            // 1. Skip metadata lines starting with "name:", "date:", etc.
            if (HEADER_LINE_PREFIXES.any { lowerLine.startsWith(it) }) {
                continue
            }

            // 2. Skip sheet titles like "Weekly Spelling Words - Week 4", "Unit 3 Spelling List"
            if (TITLE_PATTERNS.any { it.matches(trimmedLine) }) {
                continue
            }

            // Remove leading list numbers e.g. "1. elephant", "2) cat", "10. bright"
            val lineWithoutNumber = trimmedLine
                .replace(Regex("^[0-9]+[.)\\s-]+\\s*"), "")
                .replace(Regex("^[-*•>~]+\\s*"), "")
                .trim()

            // Split into tokens (comma, semicolon, or whitespace)
            val tokens = lineWithoutNumber.split(Regex("[,;\\s]+"))
            for (token in tokens) {
                val cleaned = token
                    .replace(Regex("^[^a-zA-Z]+"), "") // trim leading non-letters
                    .replace(Regex("[^a-zA-Z]+$"), "") // trim trailing non-letters
                    .lowercase()

                // Validate word: minimum 2 characters, only letters (or contraction/hyphen)
                if (cleaned.length >= 2 && cleaned.all { it.isLetter() || it == '\'' || it == '-' }) {
                    // Do not add isolated header meta words like "spelling", "phonics"
                    if (!PURE_META_WORDS.contains(cleaned)) {
                        candidateWords.add(cleaned)
                    }
                }
            }
        }

        return candidateWords.distinct()
    }

    /**
     * Optional Gemini Vision extraction via REST API
     */
    private suspend fun extractWordsWithGemini(bitmap: Bitmap, apiKey: String): List<String> = withContext(Dispatchers.IO) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "Look at this image of a spelling word list or school homework paper. Extract ONLY the spelling words. Return them as a clean list with one word per line. Do NOT include numbers, bullet points, headers like 'Week' or 'Spelling List', definitions, or punctuation. Just return the words.")
                        })
                        put(JSONObject().apply {
                            put("inlineData", JSONObject().apply {
                                put("mimeType", "image/jpeg")
                                put("data", base64Image)
                            })
                        })
                    })
                })
            })
        }

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("Gemini API HTTP ${response.code}")
        }

        val responseBody = response.body?.string() ?: return@withContext emptyList()
        val rootObj = JSONObject(responseBody)
        val candidates = rootObj.optJSONArray("candidates") ?: return@withContext emptyList()
        if (candidates.length() == 0) return@withContext emptyList()

        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.optJSONObject("content") ?: return@withContext emptyList()
        val parts = content.optJSONArray("parts") ?: return@withContext emptyList()
        if (parts.length() == 0) return@withContext emptyList()

        val text = parts.getJSONObject(0).optString("text", "")
        return@withContext parseSpellingWordsFromText(text)
    }

    /**
     * Utility to load a Bitmap from a content Uri
     */
    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
                android.graphics.ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.isMutableRequired = true
                }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load bitmap from URI", e)
            null
        }
    }
}
