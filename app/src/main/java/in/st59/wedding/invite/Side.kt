package `in`.st59.wedding.invite

enum class DaySet(val id: String) {
    ALL("all"),
    D29_30("29-30"),
    D30("30"),
}

enum class HostSide(
    val id: String,
    val label: String,
    val host: String,
    val siteUrl: String,
    val inviteFrom: String,
) {
    GROOM(
        id = "groom",
        label = "Somya’s family",
        host = "wedding.st59.in",
        siteUrl = "https://wedding.st59.in",
        inviteFrom = "Pankaj Tiwari & Family",
    ),
    BRIDE(
        id = "bride",
        label = "Yashika’s family",
        host = "wedding.yashikabum.in",
        siteUrl = "https://wedding.yashikabum.in",
        inviteFrom = "Pratibha & Deepak Bum",
    );

    fun daysSummary(days: DaySet): String = when (this) {
        GROOM -> when (days) {
            DaySet.ALL -> "27–30 November 2026 · Indore & Ujjain"
            DaySet.D29_30 -> "29 & 30 November 2026 · Ujjain"
            DaySet.D30 -> "30 November 2026 · Ujjain (Wedding day)"
        }
        BRIDE -> when (days) {
            DaySet.ALL -> "28–30 November 2026 · Ujjain"
            DaySet.D29_30 -> "29 & 30 November 2026 · Ujjain"
            DaySet.D30 -> "30 November 2026 · Ujjain (Wedding day)"
        }
    }

    fun dayTitle(days: DaySet): Pair<String, String> = when (this) {
        GROOM -> when (days) {
            DaySet.ALL -> "All days" to "27, 28, 29 & 30 November"
            DaySet.D29_30 -> "29 & 30 only" to "Ujjain · Sagai to Wedding"
            DaySet.D30 -> "30 only" to "Wedding day"
        }
        BRIDE -> when (days) {
            DaySet.ALL -> "All days" to "28, 29 & 30 November · Ujjain"
            DaySet.D29_30 -> "29 & 30 only" to "Sagai to Wedding · Ujjain"
            DaySet.D30 -> "30 only" to "Wedding day"
        }
    }

    companion object {
        fun fromHost(host: String?): HostSide {
            val h = host.orEmpty().lowercase().removePrefix("www.")
            return if (h.contains("yashikabum")) BRIDE else GROOM
        }
    }
}

data class Guest(
    val name: String,
    val phone: String,
)
