package com.example.data.model

data class SpellerSuperpower(
    val id: String,
    val title: String,
    val emoji: String,
    val tagline: String,
    val description: String,
    val learningStrategy: String,
    val recommendedArena: String,
    val colorHex: Long
) {
    val name: String get() = title
}

object SpellerSuperpowers {
    val ALL = listOf(
        SpellerSuperpower(
            id = "sound_detective",
            title = "Sound Detective 🔍",
            emoji = "🔍",
            tagline = "Master of Phonics & Syllables",
            description = "You hear the hidden building blocks inside words! Your ears catch tricky phonemes, silent letters, and syllable rhythms before anything slips by.",
            learningStrategy = "Auditory & Phonics breakdown: Sound words out phoneme-by-phoneme and segment multisyllable words into beats.",
            recommendedArena = "🎧 Listen & Spell & 🎙️ Parent-Guided Audio",
            colorHex = 0xFF4338CA // Indigo
        ),
        SpellerSuperpower(
            id = "speedy_solver",
            title = "Speedy Solver ⚡",
            emoji = "⚡",
            tagline = "Master of Fast Patterns & Word Chunks",
            description = "You spot word families and letter chunks in a flash! Common letter combinations like -ight, -tion, and vowel digraphs instantly click for you.",
            learningStrategy = "Pattern recognition: Group letters into familiar chunks and syllables rather than sounding out letter-by-letter.",
            recommendedArena = "🔤 Word Scramble & 🧩 Missing Vowel Buster",
            colorHex = 0xFFD97706 // Amber
        ),
        SpellerSuperpower(
            id = "phonics_knight",
            title = "Phonics Knight 🛡️",
            emoji = "🛡️",
            tagline = "Guardian of Spelling Rules & Structure",
            description = "You use spelling rules as your shield! Bossy 'E', doubling consonants, soft C/G, and prefix/suffix rules protect you against tricky spelling traps.",
            learningStrategy = "Orthographic rules & logic: Apply phonics principles to confidently conquer unexpected English spelling patterns.",
            recommendedArena = "🐝 Boss Spelling Bee & 🧩 Missing Vowel",
            colorHex = 0xFF0284C7 // Sky
        ),
        SpellerSuperpower(
            id = "word_artist",
            title = "Word Artist 🎨",
            emoji = "🎨",
            tagline = "Master of Visual Memory & Word Shapes",
            description = "You paint pictures of words in your photographic memory! You remember how words look, their tall and low letter shapes, and the rhythm of writing them.",
            learningStrategy = "Orthographic mapping & visual memory: Study the visual shape of the word, close your eyes to visualize it, and practice tactile typing.",
            recommendedArena = "👁️ Look • Say • Cover • Write • Check",
            colorHex = 0xFFBE123C // Rose
        )
    )

    val DEFAULT = ALL[0]

    fun findByTitle(title: String): SpellerSuperpower {
        return ALL.find { it.title.equals(title, ignoreCase = true) || it.id.equals(title, ignoreCase = true) } ?: DEFAULT
    }

    fun getByIdOrTitle(query: String): SpellerSuperpower = findByTitle(query)
}
