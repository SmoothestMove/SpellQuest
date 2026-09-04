package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.SpellingWord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SpellQuest", appName)
  }

  @Test
  fun `test phonics and sentence blank helper`() {
    val word = SpellingWord(
      listId = 1L,
      word = "because",
      phonics = "be·cause",
      sentence = "I like apples because they are sweet."
    )
    assertEquals("be·cause", word.displayPhonics)
    assertTrue(word.sentenceWithBlank.contains("_____"))
  }
}
