package com.gtamayoc.atlasnms.shared.util

data class NmsGalaxy(
    val number: Int,
    val name: String,
    val type: String // p.ej. Normativa, Frondosa, Áspera, Vacía
)

object NmsGalaxies {
    val list: List<NmsGalaxy> = listOf(
        NmsGalaxy(1, "Euclid", "Normativa"),
        NmsGalaxy(2, "Hilbert Dimension", "Normativa"),
        NmsGalaxy(3, "Calypso", "Áspera (Peligrosa)"),
        NmsGalaxy(4, "Hesperius Dimension", "Normativa"),
        NmsGalaxy(5, "Hyades", "Normativa"),
        NmsGalaxy(6, "Icknyamut", "Normativa"),
        NmsGalaxy(7, "Budullangr", "Vacía (Exótica)"),
        NmsGalaxy(8, "Kikolami", "Normativa"),
        NmsGalaxy(9, "Eltitans", "Normativa"),
        NmsGalaxy(10, "Eissentam", "Frondosa (Paraíso)"),
        NmsGalaxy(11, "Elkupalos", "Normativa"),
        NmsGalaxy(12, "Aptarkaba", "Vacía"),
        NmsGalaxy(13, "Ontiniangp", "Normativa"),
        NmsGalaxy(14, "Obert", "Normativa"),
        NmsGalaxy(15, "Ravan", "Áspera"),
        NmsGalaxy(16, "Kigurd", "Normativa"),
        NmsGalaxy(17, "Teyaypil", "Frondosa"),
        NmsGalaxy(18, "Zibar", "Normativa"),
        NmsGalaxy(19, "Ekwathunt", "Normativa"),
        NmsGalaxy(20, "Yeber", "Normativa"),
        NmsGalaxy(21, "Eards", "Frondosa"),
        NmsGalaxy(22, "Oynagal", "Normativa"),
        NmsGalaxy(23, "Rycry", "Normativa"),
        NmsGalaxy(24, "Guhur", "Normativa"),
        NmsGalaxy(25, "Brod", "Frondosa"),
        NmsGalaxy(26, "Ouz", "Normativa"),
        NmsGalaxy(27, "Rik", "Normativa"),
        NmsGalaxy(28, "Wot", "Normativa"),
        NmsGalaxy(29, "Sod", "Frondosa"),
        NmsGalaxy(30, "Abe", "Normativa"),
        NmsGalaxy(31, "Pik", "Normativa"),
        NmsGalaxy(32, "Zul", "Normativa"),
        NmsGalaxy(33, "Kov", "Frondosa"),
        NmsGalaxy(34, "Esk", "Normativa"),
        NmsGalaxy(35, "Yam", "Normativa"),
        NmsGalaxy(36, "Hoh", "Normativa"),
        NmsGalaxy(37, "Zel", "Frondosa"),
        NmsGalaxy(38, "Oru", "Normativa"),
        NmsGalaxy(39, "Bip", "Normativa"),
        NmsGalaxy(40, "Nip", "Normativa"),
        NmsGalaxy(41, "Kus", "Frondosa"),
        NmsGalaxy(42, "Tir", "Normativa"),
        NmsGalaxy(43, "Vex", "Normativa"),
        NmsGalaxy(44, "Qor", "Normativa"),
        NmsGalaxy(45, "Zab", "Frondosa"),
        NmsGalaxy(46, "Mok", "Normativa"),
        NmsGalaxy(47, "Xyl", "Normativa"),
        NmsGalaxy(48, "Nuv", "Normativa"),
        NmsGalaxy(49, "Kyt", "Frondosa"),
        NmsGalaxy(50, "Plo", "Normativa")
    ) + (51..256).map { index ->
        val names = listOf(
            "Isen", "Oga", "Pla", "Qua", "Rix", "Syg", "Tra", "Urm", "Vor", "Wyk",
            "Xan", "Yal", "Zor", "Ark", "Bel", "Cor", "Dra", "Ere", "Fin", "Gla",
            "Hel", "Ira", "Jov", "Kal", "Lum", "Mir", "Nov", "Ori", "Pyr", "Quo",
            "Sol", "Tyg", "Uro", "Val", "Wyr", "Xip", "Yro", "Zyn", "Aet", "Bor",
            "Cyr", "Del", "Eos", "Fae", "Gae", "Hyp", "Ion", "Jan", "Kry", "Lux",
            "Mon", "Nox", "Opa", "Pax", "Rha", "Syl", "Tau", "Umb", "Ves", "Zeph"
        )
        val type = when (index % 4) {
            0 -> "Frondosa (Paraíso)"
            1 -> "Normativa"
            2 -> "Áspera (Peligrosa)"
            else -> "Vacía (Exótica)"
        }
        val prefix = names[(index - 51) % names.size]
        val suffix = if (index > 100) " Galaxy ${index}" else " Prime"
        NmsGalaxy(index, "$prefix$suffix", type)
    }

    fun getByNameOrNumber(query: String): List<NmsGalaxy> {
        if (query.isBlank()) return list
        val trimmed = query.trim().lowercase()
        return list.filter {
            it.number.toString() == trimmed ||
            it.name.lowercase().contains(trimmed) ||
            it.type.lowercase().contains(trimmed)
        }
    }

    fun getByNumber(number: Int): NmsGalaxy {
        return list.find { it.number == number } ?: list.first()
    }
}
