package com.example.data.model

data class PackWord(
    val word: String,
    val phonics: String,
    val definition: String,
    val sentence: String,
    val hint: String = ""
)

data class PredefinedGradePack(
    val title: String,
    val gradeLevel: String,
    val description: String,
    val words: List<PackWord>
)

object GradePacks {
    val allPacks = listOf(
        PredefinedGradePack(
            title = "Grade 1: Magic Sight Words & Vowels",
            gradeLevel = "1st Grade",
            description = "High-frequency elementary words with short and long vowel patterns.",
            words = listOf(
                PackWord("play", "p·lay", "To engage in activity for enjoyment", "The children want to play outside in the sunshine.", "Starts with 'pl' sound"),
                PackWord("jump", "j·ump", "To push oneself off the ground into the air", "Frogs can jump very high across the pond.", "Rhymes with pump"),
                PackWord("friend", "f·ri·end", "A person whom one knows and has a bond of affection with", "Emma shared her favorite toy with her best friend.", "Remember: Fri-end until the end"),
                PackWord("little", "lit·tle", "Small in size, amount, or degree", "The little puppy slept in a tiny basket.", "Double 'tt' in the middle"),
                PackWord("house", "h·ou·se", "A building for human habitation", "We painted our house bright blue and white.", "Contains the 'ou' diphthong"),
                PackWord("water", "wa·ter", "A clear liquid essential for all living things", "Remember to drink a cold glass of water every day.", "Sounds like 'wah-ter'"),
                PackWord("where", "wh·ere", "At, in, or to what place or position", "Do you know where the library is located?", "Starts with 'wh' question word"),
                PackWord("there", "th·ere", "In, at, or to that place or position", "Look over there at the beautiful rainbow!", "Spelled like 'here' with a 't'"),
                PackWord("school", "sch·ool", "An institution for educating children", "The yellow bus takes us safely to school every morning.", "Has a silent 'h' after 'sc'"),
                PackWord("bright", "b·ri·ght", "Giving out or reflecting a lot of light", "The morning sun was warm and bright.", "Silent 'gh' pattern")
            )
        ),
        PredefinedGradePack(
            title = "Grade 2: Blends, Digraphs & Everyday Words",
            gradeLevel = "2nd Grade",
            description = "Building phonics confidence with blends, compound words, and silent letters.",
            words = listOf(
                PackWord("because", "be·cause", "For the reason that", "We brought an umbrella because it started raining.", "Mnemonic: Big Elephants Can Always Understand Small Elephants"),
                PackWord("around", "a·round", "On every side of or located nearby", "The kids ran around the playground during recess.", "Starts with 'a-' prefix"),
                PackWord("happy", "hap·py", "Feeling or showing pleasure or contentment", "The puppy wagged its tail when it was happy.", "Double 'p' before 'y'"),
                PackWord("summer", "sum·mer", "The warmest season of the year", "We love swimming at the lake during the hot summer.", "Double 'm' in middle"),
                PackWord("kitten", "kit·ten", "A young cat", "The fluffy kitten drank milk from a bowl.", "Double 't' sound"),
                PackWord("yellow", "yel·low", "The color between green and orange in the spectrum", "The bright yellow sunflower followed the sunshine.", "Double 'l' in the middle"),
                PackWord("people", "peo·ple", "Human beings in general or considered collectively", "Many people gathered in the park for the concert.", "Tricky 'eo' spelling"),
                PackWord("laugh", "l·augh", "Make sounds and movements that show amusement", "The funny clown made everyone in the room laugh.", "Ends in 'gh' sounding like 'f'"),
                PackWord("garden", "gar·den", "A piece of ground used for growing flowers or vegetables", "Mom picked fresh red tomatoes from our backyard garden.", "Two syllables: gar-den"),
                PackWord("window", "win·dow", "An opening in a wall or door fitted with glass", "The bird tapped gently on the bedroom window.", "Ends with 'ow'")
            )
        ),
        PredefinedGradePack(
            title = "Grade 3: Tricky Patterns & Silent Letters",
            gradeLevel = "3rd Grade",
            description = "Mastering complex vowel combinations, silent consonants, and multisyllabic words.",
            words = listOf(
                PackWord("knight", "k·night", "A man granted an honorary title of knighthood", "The brave knight wore silver armor to protect the castle.", "Silent 'k' and silent 'gh'"),
                PackWord("bridge", "b·rid·ge", "A structure carrying a pathway or road across an obstacle", "We walked across the wooden bridge over the rushing river.", "Tricky 'dge' ending"),
                PackWord("island", "is·land", "A piece of land surrounded by water", "The pirate ship sailed toward a secret tropical island.", "Silent 's' in the middle"),
                PackWord("scratch", "sc·rat·ch", "Score or mark the surface of with something sharp", "Be careful not to scratch the new polished wooden table.", "Contains 'tch' trigraph"),
                PackWord("through", "th·rough", "Moving in one side and out of the other side", "The train sped through the mountain tunnel.", "Contains tricky 'ough'"),
                PackWord("though", "th·ough", "Despite the fact that; although", "She finished the race even though she was tired.", "Drop the 'r' from through"),
                PackWord("castle", "cas·tle", "A large fortified building typical of the medieval period", "The stone castle had towering walls and a wide moat.", "Silent 't' in castle"),
                PackWord("whistle", "whis·tle", "A clear high-pitched sound made by forcing breath through teeth or lips", "The soccer coach blew his whistle to end the game.", "Silent 't' in whistle"),
                PackWord("honest", "hon·est", "Free of deceit and untruthfulness; sincere", "It is always best to tell the honest truth.", "Silent 'h' at the start"),
                PackWord("autumn", "au·tumn", "The season after summer and before winter; fall", "The crisp autumn air turned the maple leaves golden red.", "Silent 'n' at the end")
            )
        ),
        PredefinedGradePack(
            title = "Grade 4: Prefixes, Suffixes & Adventure Words",
            gradeLevel = "4th Grade",
            description = "Advanced elementary vocabulary, compound patterns, and root words.",
            words = listOf(
                PackWord("mystery", "mys·te·ry", "Something that is difficult or impossible to understand or explain", "The detective solved the great mystery of the missing key.", "Spelled with 'y' in the root"),
                PackWord("beautiful", "beau·ti·ful", "Pleasing the senses or mind aesthetically", "The mountain view at sunset was completely beautiful.", "Mnemonic: B-E-A-Utiful"),
                PackWord("invisible", "in·vis·i·ble", "Unable to be seen; not visible to the eye", "The magic potion made the mischievous cat invisible.", "Prefix 'in-' means not"),
                PackWord("disappear", "dis·ap·pear", "Cease to be visible or to exist", "The magician made the coin disappear into thin air.", "Prefix 'dis-' + appear"),
                PackWord("rhythm", "rhy·thm", "A strong, regular, repeated pattern of movement or sound", "The drummer kept a steady musical rhythm for the band.", "No vowels except 'y'"),
                PackWord("dinosaur", "di·no·saur", "A fossil reptile of the Mesozoic era", "The museum featured a towering skeleton of a dinosaur.", "Ends in '-saur'"),
                PackWord("adventure", "ad·ven·ture", "An unusual and exciting, typically hazardous, experience", "The hikers set off on a thrilling mountain adventure.", "Three syllables: ad-ven-ture"),
                PackWord("discover", "dis·cov·er", "Find unexpectedly or in the course of a search", "Scientists hope to discover new species in the ocean.", "Prefix 'dis-' + cover"),
                PackWord("celebrate", "cel·e·brate", "Acknowledge a significant day or event with a social gathering", "Our family gathered to celebrate grandma's birthday.", "Soft 'c' sound at the start"),
                PackWord("language", "lan·guage", "The principal method of human communication", "Learning a second language opens up new friendships.", "Contains '-uage' ending")
            )
        ),
        PredefinedGradePack(
            title = "Grade 5: Vocabulary Champs & Latin Roots",
            gradeLevel = "5th Grade",
            description = "Challenging multisyllabic words with complex affixes and Greek/Latin origins.",
            words = listOf(
                PackWord("independent", "in·de·pen·dent", "Free from outside control; not depending on another's authority", "Students are encouraged to become independent thinkers.", "Ends in '-ent'"),
                PackWord("magnificent", "mag·nif·i·cent", "Impressively beautiful, elaborate, or extravagant", "The fireworks show above the harbor was magnificent.", "Root 'magni' means large"),
                PackWord("curiosity", "cu·ri·os·i·ty", "A strong desire to know or learn something", "Her scientific curiosity led to a fascinating experiment.", "Suffix '-ity' forms noun"),
                PackWord("guarantee", "guar·an·tee", "A formal promise or assurance that certain conditions will be fulfilled", "The store provides a full guarantee for every product sold.", "Starts with 'gua-'"),
                PackWord("appreciate", "ap·pre·ci·ate", "Recognize the full worth of; be grateful for", "I truly appreciate your thoughtful help with my homework.", "Double 'p' near the front"),
                PackWord("environment", "en·vi·ron·ment", "The surroundings or conditions in which a person, animal, or plant lives", "We all must do our part to keep our natural environment clean.", "Contains 'iron' in the middle"),
                PackWord("necessary", "nec·es·sary", "Required to be done, achieved, or present; essential", "It is necessary to wear a safety helmet when riding a bike.", "One 'c', two 's's"),
                PackWord("restaurant", "res·tau·rant", "A place where people pay to sit and eat meals", "We celebrated Friday night with pizza at our favorite restaurant.", "Contains 'tau' in middle"),
                PackWord("vocabulary", "vo·cab·u·lar·y", "The body of words used in a particular language", "Reading books every day expands your spelling vocabulary.", "Five syllables: vo-cab-u-lar-y"),
                PackWord("impossible", "im·pos·si·ble", "Not able to occur, exist, or be done", "Nothing is impossible when you practice and never give up.", "Prefix 'im-' + possible")
            )
        )
    )
}
