package `in`.st59.wedding.invite

import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract

object ContactsHelper {
    fun read(resolver: ContentResolver, uri: Uri): Guest? {
        var contactId: String? = null
        var name = ""
        resolver.query(uri, null, null, null, null)?.use { cursor ->
            if (!cursor.moveToFirst()) return null
            val idIdx = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            if (idIdx >= 0) contactId = cursor.getString(idIdx)
            if (nameIdx >= 0) name = cursor.getString(nameIdx).orEmpty().trim()
        }
        val id = contactId ?: return if (name.isNotBlank()) Guest(name, "") else null
        val phone = bestPhone(resolver, id)
        if (name.isBlank() && phone.isBlank()) return null
        return Guest(name, phone)
    }

    private fun bestPhone(resolver: ContentResolver, contactId: String): String {
        val phones = mutableListOf<String>()
        resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId),
            null,
        )?.use { cursor ->
            val idx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (cursor.moveToNext()) {
                if (idx >= 0) phones += cursor.getString(idx).orEmpty()
            }
        }
        var phone = ""
        for (value in phones) {
            val d = value.filter { it.isDigit() }
            if (d.length >= 10) {
                phone = d.takeLast(10)
                if (phone.first() in '6'..'9') return phone
            }
        }
        return phone
    }
}
