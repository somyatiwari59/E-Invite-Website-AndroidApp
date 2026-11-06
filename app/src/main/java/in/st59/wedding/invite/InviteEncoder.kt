package `in`.st59.wedding.invite

import android.util.Base64
import org.json.JSONObject

object InviteEncoder {
    fun encode(name: String, saparivar: Boolean, days: DaySet, group: Boolean = false): String {
        val payload = JSONObject()
        payload.put("n", if (group) "समस्त" else name.trim().take(80).ifEmpty { "Guest" })
        payload.put("s", if (group) true else saparivar)
        payload.put("d", days.id)
        if (group) payload.put("g", true)
        val bytes = payload.toString().toByteArray(Charsets.UTF_8)
        val token = Base64.encodeToString(
            bytes,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING,
        )
        return "c.$token"
    }

    fun inviteUrl(side: HostSide, token: String): String {
        return "${side.siteUrl}/?i=${java.net.URLEncoder.encode(token, "UTF-8")}"
    }

    fun guestLine(name: String): String {
        val n = name.trim().replace(Regex("\\s+"), " ")
        if (n.isEmpty()) return ""
        if (n.endsWith("जी") || n.lowercase().endsWith("ji")) {
            return n.replace(Regex("\\s*(जी|ji)\\s*$", RegexOption.IGNORE_CASE), " जी")
        }
        return "$n जी"
    }

    fun requestLine(saparivar: Boolean, group: Boolean): String {
        if (group) return "आप सभी सपरिवार सादर आमंत्रित हैं"
        return if (saparivar) "आप सपरिवार सादर आमंत्रित हैं" else "आप सादर आमंत्रित हैं"
    }

    fun whatsappText(
        side: HostSide,
        name: String,
        saparivar: Boolean,
        days: DaySet,
        url: String,
        group: Boolean = false,
    ): String {
        val whenLine = side.daysSummary(days)
        val details = "पूरे विवरण हेतु कृपया यहाँ देखें:"
        val from = "From ${side.inviteFrom}"
        return if (group) {
            """
            ॥ श्री गणेशाय नमः ॥
            प्रिय परिवार व मित्रों,
            ${requestLine(true, true)}।

            $whenLine

            $details
            $url

            $from
            """.trimIndent()
        } else {
            """
            ॥ श्री गणेशाय नमः ॥
            आदरणीय ${guestLine(name)},
            ${requestLine(saparivar, false)}।

            $whenLine

            $details
            $url

            $from
            """.trimIndent()
        }
    }

    fun waHref(message: String, phone: String): String {
        val text = java.net.URLEncoder.encode(message, "UTF-8").replace("+", "%20")
        val digits = phone.filter { it.isDigit() }
        val withCountry = when {
            digits.length == 10 -> "91$digits"
            digits.length >= 12 -> digits
            else -> digits
        }
        return if (withCountry.length >= 12) {
            "https://wa.me/$withCountry?text=$text"
        } else {
            "https://wa.me/?text=$text"
        }
    }
}
