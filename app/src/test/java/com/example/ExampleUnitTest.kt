package com.example

import com.example.util.WordPhotoScanner
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests verifying core business logic and OCR photo parsing.
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testPhotoScanner_parsesNumberedHomeworkList() {
    val sampleOcrText = """
      Weekly Spelling Words - Week 4
      Name: Alex
      Date: 10/12/2026
      
      1. because
      2. friend
      3. laugh
      4. enough
      5. caught
      6. bright
      7. school
      8. animal
    """.trimIndent()

    val parsed = WordPhotoScanner.parseSpellingWordsFromText(sampleOcrText)
    val expected = listOf("because", "friend", "laugh", "enough", "caught", "bright", "school", "animal")
    assertEquals(expected, parsed)
  }

  @Test
  fun testPhotoScanner_parsesBulletedAndCommaSeparatedList() {
    val sampleOcrText = """
      Spelling List:
      • elephant, giraffe, neighbor
      - dolphin, unicorn
      * wonder
    """.trimIndent()

    val parsed = WordPhotoScanner.parseSpellingWordsFromText(sampleOcrText)
    val expected = listOf("elephant", "giraffe", "neighbor", "dolphin", "unicorn", "wonder")
    assertEquals(expected, parsed)
  }
}

