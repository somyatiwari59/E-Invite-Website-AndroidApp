package `in`.st59.wedding.invite

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import `in`.st59.wedding.invite.ui.InviteApp
import `in`.st59.wedding.invite.ui.WeddingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = false

        setContent {
            WeddingTheme {
                var side by remember {
                    mutableStateOf(SideStore.resolve(this, intent.data?.host))
                }
                var sideChosen by remember {
                    mutableStateOf(SideStore.isSet(this) || !intent.data?.host.isNullOrBlank())
                }
                var name by remember { mutableStateOf("") }
                var phone by remember { mutableStateOf("") }
                var saparivar by remember { mutableStateOf(false) }
                var days by remember { mutableStateOf(DaySet.ALL) }
                var hint by remember { mutableStateOf("") }
                var queue by remember { mutableStateOf(listOf<Guest>()) }
                var group by remember { mutableStateOf(false) }

                val pickContact = rememberLauncherForActivityResult(
                    ActivityResultContracts.PickContact(),
                ) { uri: Uri? ->
                    if (uri == null) {
                        hint = "No contact selected"
                        return@rememberLauncherForActivityResult
                    }
                    val guest = ContactsHelper.read(contentResolver, uri)
                    if (guest == null) {
                        hint = "Could not read that contact"
                        return@rememberLauncherForActivityResult
                    }
                    name = guest.name
                    phone = guest.phone
                    group = false
                    hint = "Loaded ${guest.name.ifBlank { "contact" }} — send when ready"
                }

                val askContacts = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission(),
                ) { granted ->
                    if (granted) {
                        pickContact.launch(null)
                    } else {
                        hint = "Contacts permission is needed to pick a person"
                    }
                }

                InviteApp(
                    side = side,
                    sideChosen = sideChosen,
                    onSide = {
                        side = it
                        SideStore.save(this, it)
                        sideChosen = true
                    },
                    name = name,
                    onName = { name = it },
                    phone = phone,
                    onPhone = { phone = it },
                    saparivar = saparivar,
                    onSaparivar = { saparivar = it },
                    days = days,
                    onDays = { days = it },
                    group = group,
                    onGroup = { group = it },
                    hint = hint,
                    queue = queue,
                    onPickContact = {
                        val ok = ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_CONTACTS,
                        ) == PackageManager.PERMISSION_GRANTED
                        if (ok) pickContact.launch(null) else askContacts.launch(Manifest.permission.READ_CONTACTS)
                    },
                    onAddToQueue = {
                        val guest = Guest(name.trim(), phone.trim())
                        if (guest.name.length < 2) {
                            hint = "Pick or type a guest first"
                            return@InviteApp
                        }
                        queue = queue + guest
                        name = ""
                        phone = ""
                        hint = "${guest.name} added. Pick the next person."
                    },
                    onRemoveQueue = { guest ->
                        queue = queue.filterNot { it.name == guest.name && it.phone == guest.phone }
                    },
                    onSend = { guest ->
                        val sendName = guest?.name ?: name
                        val sendPhone = guest?.phone ?: phone
                        val token = InviteEncoder.encode(sendName, saparivar, days, group)
                        val url = InviteEncoder.inviteUrl(side, token)
                        val msg = InviteEncoder.whatsappText(side, sendName, saparivar, days, url, group)
                        startActivity(Intent(Intent.ACTION_VIEW, InviteEncoder.waHref(msg, sendPhone).toUri()))
                        hint = "Opened WhatsApp for ${if (group) "group" else sendName}"
                    },
                    onSendQueueNext = {
                        val next = queue.firstOrNull() ?: return@InviteApp
                        val token = InviteEncoder.encode(next.name, saparivar, days, false)
                        val url = InviteEncoder.inviteUrl(side, token)
                        val msg = InviteEncoder.whatsappText(side, next.name, saparivar, days, url, false)
                        startActivity(Intent(Intent.ACTION_VIEW, InviteEncoder.waHref(msg, next.phone).toUri()))
                        queue = queue.drop(1)
                        hint = "Opened WhatsApp for ${next.name}. ${queue.size} left."
                    },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.data?.host?.let { SideStore.resolve(this, it) }
    }
}
