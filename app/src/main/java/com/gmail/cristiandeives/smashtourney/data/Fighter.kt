package com.gmail.cristiandeives.smashtourney.data

data class Fighter(
    val number: Int = 0,
    val name: String = "",
    val isEcho: Boolean = false
) {
    companion object {
        val DEFAULT_FIGHTERS = listOf(
            Fighter(1, "Mario"),
            Fighter(2, "Donkey Kong"),
            Fighter(3, "Link"),
            Fighter(4, "Samus"),
            Fighter(4, "Dark Samus", true),
            Fighter(5, "Yoshi"),
            Fighter(6, "Kirby"),
            Fighter(7, "Fox"),
            Fighter(8, "Pikachu"),
            Fighter(9, "Luigi"),
            Fighter(10, "Ness"),
            Fighter(11, "Captain Falcon"),
            Fighter(12, "Jigglypuff"),
            Fighter(13, "Peach"),
            Fighter(13, "Daisy", true),
            Fighter(14, "Bowser"),
            Fighter(15, "Ice Climbers"),
            Fighter(16, "Sheik"),
            Fighter(17, "Zelda"),
            Fighter(18, "Dr. Mario"),
            Fighter(19, "Pichu"),
            Fighter(20, "Falco"),
            Fighter(21, "Marth"),
            Fighter(21, "Lucina", true),
            Fighter(22, "Young Link"),
            Fighter(23, "Ganondorf"),
            Fighter(24, "Mewtwo"),
            Fighter(25, "Roy"),
            Fighter(25, "Chrom", true),
            Fighter(26, "Mr. Game & Watch"),
            Fighter(27, "Meta Knight"),
            Fighter(28, "Pit"),
            Fighter(28, "Dark Pit", true),
            Fighter(29, "Zero Suit Samus"),
            Fighter(30, "Wario"),
            Fighter(31, "Snake"),
            Fighter(32, "Ike"),
            Fighter(33, "Pokémon Trainer (Squirtle)"),
            Fighter(34, "Pokémon Trainer (Ivysaur)"),
            Fighter(35, "Pokémon Trainer (Charizard)"),
            Fighter(36, "Diddy Kong"),
            Fighter(37, "Lucas"),
            Fighter(38, "Sonic"),
            Fighter(39, "King Dedede"),
            Fighter(40, "Olimar"),
            Fighter(41, "Lucario"),
            Fighter(42, "R.O.B."),
            Fighter(43, "Toon Link"),
            Fighter(44, "Wolf"),
            Fighter(45, "Villager"),
            Fighter(46, "Mega Man"),
            Fighter(47, "Wii Fit Trainer"),
            Fighter(48, "Rosalina & Luma"),
            Fighter(49, "Little Mac"),
            Fighter(50, "Greninja"),
            Fighter(51, "Mii Fighter (Brawler)"),
            Fighter(52, "Mii Fighter (Swordfighter)"),
            Fighter(53, "Mii Fighter (Gunner)"),
            Fighter(54, "Palutena"),
            Fighter(55, "Pac-Man"),
            Fighter(56, "Robin"),
            Fighter(57, "Shulk"),
            Fighter(58, "Bowser Jr."),
            Fighter(59, "Duck Hunt"),
            Fighter(60, "Ryu"),
            Fighter(60, "Ken", true),
            Fighter(61, "Cloud"),
            Fighter(62, "Corrin"),
            Fighter(63, "Bayonetta"),
            Fighter(64, "Inkling"),
            Fighter(65, "Ridley"),
            Fighter(66, "Simon"),
            Fighter(66, "Richter", true),
            Fighter(67, "King K. Rool"),
            Fighter(68, "Isabelle"),
            Fighter(69, "Incineroar"),
            Fighter(70, "Piranha Plant"),
            Fighter(71, "Joker"),
            Fighter(72, "Hero")
        )

        val RANDOM = Fighter(-1, "Random")
    }
}