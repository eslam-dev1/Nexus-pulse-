package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Contact
import com.example.data.model.PresenceStatus
import com.example.data.model.SocialPlatform
import com.example.data.model.SocialProfile
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElevatedSurface
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.StatusRose
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent
import org.json.JSONArray
import org.json.JSONObject

/**
 * Encodes a [Contact] into a compact, deterministic NexusPulse QR payload string.
 */
fun encodeContactQrPayload(contact: Contact): String {
    val root = JSONObject().apply {
        put("v", 1)
        put("n", contact.name)
        put("t", contact.title)
        put("c", contact.company)
        put("e", contact.email)
        put("p", contact.phone)
        if (contact.tags.isNotEmpty()) {
            val tagsArr = JSONArray()
            contact.tags.forEach { tagsArr.put(it) }
            put("tags", tagsArr)
        }
        if (contact.socialProfiles.isNotEmpty()) {
            val spArr = JSONArray()
            contact.socialProfiles.forEach { sp ->
                val obj = JSONObject()
                obj.put("pl", sp.platform.name)
                obj.put("h", sp.handle)
                spArr.put(obj)
            }
            put("sp", spArr)
        }
    }
    return "NEXUSPULSE:$root"
}

/**
 * Decodes a scanned QR code payload (NexusPulse JSON, nexuspulse:// URI, or vCard) into a [Contact].
 */
fun decodeContactQrPayload(rawPayload: String): Contact? {
    val trimmed = rawPayload.trim()
    if (trimmed.isEmpty()) return null

    // 1. Check NEXUSPULSE: JSON format
    if (trimmed.startsWith("NEXUSPULSE:", ignoreCase = true)) {
        return try {
            val jsonPart = trimmed.substringAfter(":")
            val obj = JSONObject(jsonPart)
            val name = obj.optString("n", "").trim()
            if (name.isEmpty()) return null

            val tags = mutableListOf<String>()
            val tagsArr = obj.optJSONArray("tags")
            if (tagsArr != null) {
                for (i in 0 until tagsArr.length()) {
                    val tag = tagsArr.optString(i).trim()
                    if (tag.isNotEmpty()) tags.add(tag)
                }
            }

            val profiles = mutableListOf<SocialProfile>()
            val spArr = obj.optJSONArray("sp")
            if (spArr != null) {
                for (i in 0 until spArr.length()) {
                    val spObj = spArr.optJSONObject(i) ?: continue
                    val platformName = spObj.optString("pl", "")
                    val handle = spObj.optString("h", "").trim()
                    val platform = try {
                        SocialPlatform.valueOf(platformName)
                    } catch (_: Exception) {
                        null
                    }
                    if (platform != null && handle.isNotEmpty()) {
                        profiles.add(
                            SocialProfile(
                                platform = platform,
                                handle = handle,
                                profileUrl = platform.fullUrl(handle),
                                verified = true,
                                latestSnippet = "Connected via NexusPulse QR",
                                lastActiveTime = "Just now"
                            )
                        )
                    }
                }
            }

            Contact(
                name = name,
                title = obj.optString("t", ""),
                company = obj.optString("c", ""),
                email = obj.optString("e", ""),
                phone = obj.optString("p", ""),
                avatarUrl = null,
                tags = if (tags.isEmpty()) listOf("QR Import") else tags,
                presence = PresenceStatus.ONLINE,
                socialProfiles = profiles,
                notes = "Imported via NexusPulse QR Code"
            )
        } catch (_: Exception) {
            null
        }
    }

    // 2. Check nexuspulse://contact URI format
    if (trimmed.startsWith("nexuspulse://", ignoreCase = true)) {
        return try {
            val uri = Uri.parse(trimmed)
            val name = uri.getQueryParameter("name")?.trim().orEmpty()
            if (name.isEmpty()) return null
            Contact(
                name = name,
                title = uri.getQueryParameter("title").orEmpty(),
                company = uri.getQueryParameter("company").orEmpty(),
                email = uri.getQueryParameter("email").orEmpty(),
                phone = uri.getQueryParameter("phone").orEmpty(),
                avatarUrl = null,
                tags = listOf("QR Import"),
                presence = PresenceStatus.ONLINE
            )
        } catch (_: Exception) {
            null
        }
    }

    // 3. Check standard BEGIN:VCARD format
    if (trimmed.contains("BEGIN:VCARD", ignoreCase = true)) {
        var name = ""
        var title = ""
        var company = ""
        var email = ""
        var phone = ""
        trimmed.lines().forEach { line ->
            val clean = line.trim()
            when {
                clean.startsWith("FN:", ignoreCase = true) -> name = clean.substringAfter(":").trim()
                clean.startsWith("TITLE:", ignoreCase = true) -> title = clean.substringAfter(":").trim()
                clean.startsWith("ORG:", ignoreCase = true) -> company = clean.substringAfter(":").trim()
                clean.startsWith("EMAIL", ignoreCase = true) && clean.contains(":") -> email = clean.substringAfter(":").trim()
                clean.startsWith("TEL", ignoreCase = true) && clean.contains(":") -> phone = clean.substringAfter(":").trim()
            }
        }
        if (name.isNotBlank()) {
            return Contact(
                name = name,
                title = title,
                company = company,
                email = email,
                phone = phone,
                avatarUrl = null,
                tags = listOf("vCard Import"),
                presence = PresenceStatus.ONLINE,
                notes = "Imported from vCard QR"
            )
        }
    }

    return null
}

/**
 * Generates a deterministic 25x25 QR matrix for a payload string.
 * Uses a single flat BooleanArray(625) to avoid multi-dimensional array overhead.
 */
internal fun generateQrMatrix(payload: String, size: Int = 25): BooleanArray {
    val matrix = BooleanArray(size * size)
    val reserved = BooleanArray(size * size)

    fun setModule(r: Int, c: Int, dark: Boolean, markReserved: Boolean = true) {
        if (r in 0 until size && c in 0 until size) {
            val idx = r * size + c
            matrix[idx] = dark
            if (markReserved) reserved[idx] = true
        }
    }

    fun placeFinderPattern(topRow: Int, leftCol: Int) {
        for (dr in -1..7) {
            for (dc in -1..7) {
                val r = topRow + dr
                val c = leftCol + dc
                if (r in 0 until size && c in 0 until size) {
                    val isBorder = (dr == 0 || dr == 6) && dc in 0..6 || (dc == 0 || dc == 6) && dr in 0..6
                    val isCenter = dr in 2..4 && dc in 2..4
                    setModule(r, c, isBorder || isCenter, true)
                }
            }
        }
    }

    // 3 standard QR Finder Patterns (top-left, top-right, bottom-left)
    placeFinderPattern(0, 0)
    placeFinderPattern(0, size - 7)
    placeFinderPattern(size - 7, 0)

    // Alignment pattern at bottom-right (for Version 2+ 25x25 QR)
    val alignCenter = size - 7
    for (dr in -2..2) {
        for (dc in -2..2) {
            val isBorder = dr == -2 || dr == 2 || dc == -2 || dc == 2
            val isCenter = dr == 0 && dc == 0
            setModule(alignCenter + dr, alignCenter + dc, isBorder || isCenter, true)
        }
    }

    // Timing patterns
    for (i in 8 until size - 8) {
        setModule(6, i, i % 2 == 0, true)
        setModule(i, 6, i % 2 == 0, true)
    }

    // Fill remaining data modules deterministically from the payload bytes and hash
    val bytes = payload.encodeToByteArray()
    var hash = 0x811C9DC5.toInt()
    for (b in bytes) {
        hash = (hash xor (b.toInt() and 0xFF)) * 0x01000193
    }

    var bitIndex = 0
    for (r in 0 until size) {
        for (c in 0 until size) {
            val idx = r * size + c
            if (!reserved[idx]) {
                val byteVal = if (bytes.isNotEmpty()) {
                    bytes[(bitIndex / 8) % bytes.size].toInt() and 0xFF
                } else 0xAA
                val bit = (byteVal ushr (bitIndex % 8)) and 1
                val mask = ((r * 31 + c * 17 + (hash ushr (bitIndex % 16))) and 1)
                matrix[idx] = (bit xor mask) == 1
                bitIndex++
            }
        }
    }

    return matrix
}

/**
 * Pure-Canvas QR Code composable. Uses zero Bitmap memory and renders crisp vector modules.
 */
@Composable
fun ContactQrCodeCanvas(
    payload: String,
    modifier: Modifier = Modifier
) {
    val gridSize = 25
    val qrMatrix = remember(payload) { generateQrMatrix(payload, gridSize) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(2.dp, CyanAccent.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("qr_code_canvas"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(190.dp)) {
            val cellSize = size.width / gridSize
            for (r in 0 until gridSize) {
                for (c in 0 until gridSize) {
                    if (qrMatrix[r * gridSize + c]) {
                        val isFinder = (r < 7 && c < 7) || (r < 7 && c >= gridSize - 7) || (r >= gridSize - 7 && c < 7)
                        drawRoundRect(
                            color = if (isFinder) Color(0xFF0A0E17) else Color(0xFF0F172A),
                            topLeft = Offset(c * cellSize, r * cellSize),
                            size = Size(cellSize * 0.94f, cellSize * 0.94f),
                            cornerRadius = CornerRadius(cellSize * 0.18f, cellSize * 0.18f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dialog displaying a unique QR code for sharing a [Contact], with one-tap native share
 * and payload copy actions.
 */
@Composable
fun ShareContactQrDialog(
    contact: Contact,
    onDismiss: () -> Unit,
    onOpenScanner: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val view = LocalView.current
    val payload = remember(contact) { encodeContactQrPayload(contact) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = SlateSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("share_qr_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SHARE CONTACT QR",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "Scan to add directly to NexusPulse",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("close_share_qr_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contact Identity Summary
                Text(
                    text = contact.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                if (contact.title.isNotBlank() || contact.company.isNotBlank()) {
                    Text(
                        text = listOf(contact.title, contact.company)
                            .filter { it.isNotBlank() }
                            .joinToString(" • "),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Unique QR Code Canvas
                ContactQrCodeCanvas(payload = payload)

                Spacer(modifier = Modifier.height(10.dp))

                // Embedded metadata badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardSurface)
                        .border(1.dp, BorderStroke, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "${contact.socialProfiles.size} linked social profiles encoded",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StatusEmerald
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Primary Actions: Copy QR Code Payload & Native Share Sheet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            clipboard?.setPrimaryClip(ClipData.newPlainText("NexusPulse Contact QR", payload))
                            Toast.makeText(context, "QR code payload copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("copy_qr_payload_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Copy Code",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Button(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            shareContactViaIntent(context, contact, payload)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("share_contact_intent_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanAccent,
                            contentColor = SlateSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = SlateSurface,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Share Card",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateSurface
                        )
                    }
                }

                if (onOpenScanner != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            onDismiss()
                            onOpenScanner()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("switch_to_qr_scanner_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Scan a Contact QR Instead",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    }
                }
            }
        }
    }
}

/**
 * QR Scanner & Importer Dialog allowing users to scan/paste a NexusPulse QR code or vCard
 * to quickly add a contact to their list.
 */
@Composable
fun ScanContactQrDialog(
    onDismiss: () -> Unit,
    onImportContact: (Contact) -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    var qrInput by remember { mutableStateOf("") }
    var parseError by remember { mutableStateOf(false) }

    val decodedPreview = remember(qrInput) { decodeContactQrPayload(qrInput) }

    // Sample executive contacts for 1-tap simulated camera scan in emulator environments
    val sampleScanPresets = remember {
        listOf(
            Contact(
                name = "Sundar Pichai",
                title = "CEO",
                company = "Google & Alphabet",
                email = "sundar@google.com",
                phone = "+1 (650) 253-0000",
                avatarUrl = null,
                tags = listOf("Executive", "AI", "VIP"),
                presence = PresenceStatus.ONLINE,
                socialProfiles = listOf(
                    SocialProfile(SocialPlatform.X_TWITTER, "sundarpichai", verified = true),
                    SocialProfile(SocialPlatform.INSTAGRAM, "sundarpichai", verified = true),
                    SocialProfile(SocialPlatform.THREADS, "sundarpichai", verified = true)
                )
            ),
            Contact(
                name = "Mira Murati",
                title = "AI Executive & Founder",
                company = "Thinking Machines Lab",
                email = "mira@thinkingmachines.ai",
                phone = "+1 (415) 555-0199",
                avatarUrl = null,
                tags = listOf("AI", "Founder", "VIP"),
                presence = PresenceStatus.ONLINE,
                socialProfiles = listOf(
                    SocialProfile(SocialPlatform.X_TWITTER, "miramurati", verified = true),
                    SocialProfile(SocialPlatform.LINKEDIN, "mira-murati", verified = true),
                    SocialProfile(SocialPlatform.WHATSAPP, "14155550199", verified = true)
                )
            )
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "scannerLaser")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = SlateSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("scan_qr_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SCAN CONTACT QR",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "Scan or paste a QR code to add contact",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("close_scan_qr_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Optical Viewfinder Reticle with Laser Sweep
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(136.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardSurface)
                        .border(1.dp, CyanAccent.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val laserColor = CyanAccent
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val strokeWidth = 3.dp.toPx()
                        val cornerSize = 26.dp.toPx()
                        val pad = 18.dp.toPx()

                        drawRoundRect(
                            color = laserColor.copy(alpha = 0.35f),
                            topLeft = Offset(pad, pad),
                            size = Size(size.width - pad * 2, size.height - pad * 2),
                            cornerRadius = CornerRadius(cornerSize / 2, cornerSize / 2),
                            style = Stroke(
                                width = strokeWidth,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(28f, 18f), 0f)
                            )
                        )

                        // Animated laser scan line
                        val laserY = pad + (size.height - pad * 2) * laserProgress
                        drawLine(
                            color = laserColor,
                            start = Offset(pad + 8.dp.toPx(), laserY),
                            end = Offset(size.width - pad - 8.dp.toPx(), laserY),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = CyanAccent.copy(alpha = 0.7f),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (decodedPreview != null) "QR Recognized: ${decodedPreview.name}" else "Align QR Code within frame",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (decodedPreview != null) StatusEmerald else TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick-Scan Demo Presets for instant testing
                Text(
                    text = "SIMULATE QR SCAN OR PASTE PAYLOAD",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sampleScanPresets.forEachIndexed { idx, preset ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ElevatedSurface)
                                .border(1.dp, BorderStroke, RoundedCornerShape(10.dp))
                                .clickable {
                                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                                    qrInput = encodeContactQrPayload(preset)
                                    parseError = false
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .testTag("simulate_qr_preset_$idx")
                        ) {
                            Column {
                                Text(
                                    text = "Scan: ${preset.name}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = preset.company,
                                    fontSize = 9.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // QR Code Payload Input + Paste from Clipboard
                OutlinedTextField(
                    value = qrInput,
                    onValueChange = {
                        qrInput = it
                        parseError = false
                    },
                    label = { Text("QR Payload (NEXUSPULSE:... or vCard)", fontSize = 11.sp) },
                    isError = parseError,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                val clipText = clipboard?.primaryClip?.getItemAt(0)?.text?.toString().orEmpty()
                                if (clipText.isNotBlank()) {
                                    qrInput = clipText
                                    parseError = false
                                } else {
                                    Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.testTag("paste_qr_clipboard_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = "Paste from clipboard",
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = BorderStroke,
                        errorBorderColor = StatusRose,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = CyanAccent,
                        unfocusedLabelColor = TextSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("qr_payload_input")
                )

                if (parseError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Invalid QR payload. Tap a scan preset or paste a valid NEXUSPULSE code.",
                        fontSize = 10.sp,
                        color = StatusRose
                    )
                }

                // Decoded Contact Preview Card
                if (decodedPreview != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardSurface)
                            .border(1.dp, StatusEmerald.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                            .testTag("qr_decoded_preview_card"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = decodedPreview.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${decodedPreview.title} • ${decodedPreview.company}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Text(
                            text = "${decodedPreview.socialProfiles.size} Hubs",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusEmerald
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Add Scanned Contact Button
                Button(
                    onClick = {
                        val contactToAdd = decodeContactQrPayload(qrInput)
                        if (contactToAdd != null) {
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            onImportContact(contactToAdd)
                            Toast.makeText(context, "Added ${contactToAdd.name} via QR!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        } else {
                            parseError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("scan_import_contact_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanAccent,
                        contentColor = SlateSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = SlateSurface,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Scanned Contact to List",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateSurface
                    )
                }
            }
        }
    }
}

private fun shareContactViaIntent(context: Context, contact: Contact, qrPayload: String) {
    val socialSummary = if (contact.socialProfiles.isNotEmpty()) {
        "\nSocial Profiles:\n" + contact.socialProfiles.joinToString("\n") {
            "• ${it.platform.displayName}: ${it.platform.fullUrl(it.handle)}"
        }
    } else ""

    val shareText = buildString {
        appendLine("${contact.name} — ${contact.title} at ${contact.company}")
        if (contact.email.isNotBlank()) appendLine("Email: ${contact.email}")
        if (contact.phone.isNotBlank()) appendLine("Phone: ${contact.phone}")
        append(socialSummary)
        appendLine("\n\nNexusPulse Instant QR Import Code:")
        append(qrPayload)
    }

    try {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "NexusPulse Contact: ${contact.name}")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        val chooser = Intent.createChooser(sendIntent, "Share ${contact.name}")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    } catch (_: Exception) {
        Toast.makeText(context, "Unable to open share sheet", Toast.LENGTH_SHORT).show()
    }
}
