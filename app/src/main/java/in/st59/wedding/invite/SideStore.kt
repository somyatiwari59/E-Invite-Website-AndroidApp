package `in`.st59.wedding.invite

import android.content.Context

private const val PREFS = "invite_prefs"
private const val KEY_SIDE = "host_side"

object SideStore {
    fun load(context: Context): HostSide? {
        val id = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_SIDE, null) ?: return null
        return HostSide.entries.find { it.id == id }
    }

    fun save(context: Context, side: HostSide) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SIDE, side.id)
            .apply()
    }

    fun resolve(context: Context, host: String?): HostSide {
        if (!host.isNullOrBlank()) {
            val fromLink = HostSide.fromHost(host)
            save(context, fromLink)
            return fromLink
        }
        return load(context) ?: HostSide.GROOM
    }

    fun isSet(context: Context): Boolean {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).contains(KEY_SIDE)
    }
}
