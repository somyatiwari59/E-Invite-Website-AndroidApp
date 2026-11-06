package `in`.st59.wedding.invite.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.st59.wedding.invite.DaySet
import `in`.st59.wedding.invite.Guest
import `in`.st59.wedding.invite.HostSide
import `in`.st59.wedding.invite.InviteEncoder

@Composable
fun InviteApp(
    side: HostSide,
    sideChosen: Boolean,
    onSide: (HostSide) -> Unit,
    name: String,
    onName: (String) -> Unit,
    phone: String,
    onPhone: (String) -> Unit,
    saparivar: Boolean,
    onSaparivar: (Boolean) -> Unit,
    days: DaySet,
    onDays: (DaySet) -> Unit,
    group: Boolean,
    onGroup: (Boolean) -> Unit,
    hint: String,
    queue: List<Guest>,
    onPickContact: () -> Unit,
    onAddToQueue: () -> Unit,
    onRemoveQueue: (Guest) -> Unit,
    onSend: (Guest?) -> Unit,
    onSendQueueNext: () -> Unit,
) {
    var showSettings by remember { mutableStateOf(!sideChosen) }

    Surface(modifier = Modifier.fillMaxSize()) {
        val screen = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding()

        if (showSettings || !sideChosen) {
            SettingsScreen(
                modifier = screen,
                side = side,
                firstTime = !sideChosen,
                onSave = { chosen ->
                    onSide(chosen)
                    showSettings = false
                },
                onBack = if (sideChosen) ({ showSettings = false }) else null,
            )
        } else {
            Column(
                modifier = screen
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Blessing()
                        Text("Send Invites", style = MaterialTheme.typography.headlineMedium)
                    }
                    TextButton(onClick = { showSettings = true }) {
                        Text("Settings", fontSize = 18.sp)
                    }
                }
                Text(
                    "Sending from ${side.label}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BigChip("One guest", !group) { onGroup(false) }
                    BigChip("Group", group) { onGroup(true) }
                }

                if (!group) {
                    BigButton("Pick from contacts", onPickContact)
                    OutlinedTextField(
                        value = name,
                        onValueChange = onName,
                        label = { Text("Name", fontSize = 18.sp) },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = onPhone,
                        label = { Text("WhatsApp number", fontSize = 18.sp) },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }

                Text("Invite as", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BigChip("अकेले", !saparivar && !group, enabled = !group) { onSaparivar(false) }
                    BigChip("सपरिवार", saparivar || group, enabled = !group) { onSaparivar(true) }
                }

                Text("Days", style = MaterialTheme.typography.titleMedium)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DaySet.entries.forEach { item ->
                        val (title, sub) = side.dayTitle(item)
                        FilterChip(
                            selected = days == item,
                            onClick = { onDays(item) },
                            label = {
                                Text("$title · $sub", fontSize = 18.sp, lineHeight = 24.sp)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        )
                    }
                }

                if (!group) {
                    OutlinedButton(
                        onClick = onAddToQueue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    ) {
                        Text("Add to send list", fontSize = 20.sp)
                    }
                }

                if (queue.isNotEmpty()) {
                    Text("${queue.size} in send list", style = MaterialTheme.typography.titleMedium)
                    queue.forEach { guest ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                "${guest.name}  ${guest.phone}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f),
                            )
                            OutlinedButton(
                                onClick = { onRemoveQueue(guest) },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                            ) { Text("Remove", fontSize = 18.sp) }
                        }
                    }
                    BigButton("WhatsApp next in list", onSendQueueNext)
                }

                val previewName = if (group) "परिवार व मित्रों" else InviteEncoder.guestLine(name)
                Text("Preview", style = MaterialTheme.typography.titleMedium)
                Text(
                    if (group) "आप सभी सपरिवार सादर आमंत्रित हैं"
                    else "आदरणीय $previewName · ${if (saparivar) "सपरिवार" else "अकेले"}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    side.daysSummary(days),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (hint.isNotBlank()) {
                    Text(hint, style = MaterialTheme.typography.bodyLarge)
                }

                BigButton(
                    text = if (group) "Send group WhatsApp" else "Send WhatsApp",
                    onClick = { onSend(null) },
                    enabled = group || name.trim().length > 1,
                    modifier = Modifier.padding(bottom = 28.dp),
                )
            }
        }
    }
}

@Composable
private fun Blessing() {
    Text(
        "॥ श्री गणेशाय नमः ॥",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp, top = 4.dp),
    )
}

@Composable
private fun BigButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        colors = ButtonDefaults.buttonColors(),
    ) {
        Text(text, fontSize = 22.sp)
    }
}

@Composable
private fun BigChip(
    label: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        label = { Text(label, fontSize = 20.sp) },
        modifier = Modifier.height(52.dp),
    )
}

@Composable
private fun SettingsScreen(
    modifier: Modifier,
    side: HostSide,
    firstTime: Boolean,
    onSave: (HostSide) -> Unit,
    onBack: (() -> Unit)?,
) {
    var draft by remember { mutableStateOf(side) }
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Blessing()
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Text(
            if (firstTime) "Choose whose side this phone is sending from. You only need to do this once."
            else "Change only if this phone should send from the other family.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text("Sending as", style = MaterialTheme.typography.titleMedium)
        HostSide.entries.forEach { item ->
            FilterChip(
                selected = draft == item,
                onClick = { draft = item },
                label = {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(item.label, fontSize = 22.sp)
                        Text(item.host, fontSize = 16.sp)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        BigButton("Save", onClick = { onSave(draft) })
        if (onBack != null) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Text("Back", fontSize = 20.sp)
            }
        }
    }
}
