package com.example.vmpshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ---------------- palette, matched to VMP ---------------- */

private val Bg        = Color(0xFF0B0B10)
private val Surface   = Color(0xFF16161D)
private val SurfaceHi = Color(0xFF1E1E27)
private val Accent    = Color(0xFF6C4CE0)
private val AccentHi  = Color(0xFF8A6DF0)
private val TextHi    = Color(0xFFF2F2F5)
private val TextMid   = Color(0xFF9A9AA8)
private val TextLow   = Color(0xFF5E5E6B)
private val Line      = Color(0xFF24242E)

/* ---------------- the signed-in user ---------------- */

private const val MY_NAME   = "Sonny"
private const val MY_HANDLE = "@sushi2"
private const val MY_NUMBER = "0221-3446-28"

private enum class Screen { Feed, Invite, Welcome }

private data class Post(
    val id: String,
    val author: String,
    val handle: String,
    val time: String,
    val body: String,
    val tint: Color,
    val likes: Int
)

private val posts = listOf(
    Post(
        id = "8812",
        author = "Rafiq Hasan",
        handle = "@rafiq",
        time = "2h",
        body = "Sunrise over the Padma this morning. Worth the 5am alarm.",
        tint = Color(0xFF2A3A52),
        likes = 48
    ),
    Post(
        id = "8809",
        author = "Nusrat A.",
        handle = "@nusrat",
        time = "5h",
        body = "Found a place in Rajshahi that does proper filter coffee. Finally.",
        tint = Color(0xFF3A2A42),
        likes = 112
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(background = Bg, surface = Surface)
            ) {
                App()
            }
        }
    }
}

@Composable
private fun App() {
    var screen by remember { mutableStateOf(Screen.Feed) }

    Box(
        Modifier
            .fillMaxSize()
            .background(Bg)
    ) {
        when (screen) {
            Screen.Feed    -> FeedScreen(onInvite = { screen = Screen.Invite })
            Screen.Invite  -> InviteScreen(
                onBack = { screen = Screen.Feed },
                onPreviewRecipient = { screen = Screen.Welcome }
            )
            Screen.Welcome -> WelcomeScreen(onDone = { screen = Screen.Feed })
        }
    }
}

/* ======================= 1. FEED ======================= */

@Composable
private fun FeedScreen(onInvite: () -> Unit) {
    val context = LocalContext.current

    Column(Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 12.dp, top = 52.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Feed", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextHi)
            Spacer(Modifier.weight(1f))
            Text(
                "Invite",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AccentHi,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onInvite() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }

        Row(
            modifier = Modifier.padding(start = 20.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("All" to true, "Following" to false, "Video" to false)
                .forEach { (label, active) ->
                    Text(
                        label,
                        fontSize = 13.sp,
                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (active) TextHi else TextLow,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (active) SurfaceHi else Color.Transparent)
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            posts.forEach { post ->
                PostCard(post) {
                    Share.send(
                        context = context,
                        text = Share.post(
                            author = post.author,
                            handle = post.handle,
                            body = post.body,
                            postId = post.id,
                            sharer = MY_HANDLE
                        ),
                        chooserTitle = "Share this post"
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        BottomBar()
    }
}

@Composable
private fun PostCard(post: Post, onShare: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .padding(bottom = 4.dp)
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Accent.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    post.author.first().toString(),
                    color = TextHi,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(11.dp))
            Column {
                Text(post.author, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextHi)
                Text("${post.handle} · ${post.time}", fontSize = 12.sp, color = TextLow)
            }
        }

        Text(
            post.body,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            color = TextHi.copy(alpha = 0.92f),
            modifier = Modifier.padding(horizontal = 14.dp)
        )

        Spacer(Modifier.height(12.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(170.dp)
                .padding(horizontal = 14.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(post.tint)
        )

        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Action(Icons.Default.FavoriteBorder, "${post.likes}", TextMid) { }
            Spacer(Modifier.weight(1f))
            Action(Icons.Default.Share, "Share", AccentHi, onShare)
        }
    }
}

@Composable
private fun Action(icon: ImageVector, label: String, tint: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(7.dp))
        Text(label, fontSize = 13.sp, color = tint, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun BottomBar() {
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.Email,
        Icons.Default.Call,
        Icons.Default.Person,
        Icons.Default.ShoppingCart,
        Icons.Default.Settings
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .padding(top = 10.dp, bottom = 34.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icons.forEachIndexed { i, icon ->
            Box(
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (i == 0) Accent.copy(alpha = 0.3f) else Color.Transparent)
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (i == 0) AccentHi else TextLow,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}

/* ======================= 2. INVITE ======================= */

@Composable
private fun InviteScreen(onBack: () -> Unit, onPreviewRecipient: () -> Unit) {
    val context = LocalContext.current
    val message = Share.invite(MY_NAME, MY_HANDLE, MY_NUMBER)

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 52.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = TextHi,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onBack() }
                    .padding(12.dp)
                    .size(22.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text("Invite friends", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextHi)
        }

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
        ) {
            Text(
                "They'll see who it came from and can add you the moment they open the app.",
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color = TextMid
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "PREVIEW",
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextLow
            )

            Spacer(Modifier.height(10.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp))
                    .background(Accent.copy(alpha = 0.18f))
                    .border(
                        1.dp,
                        Accent.copy(alpha = 0.35f),
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(message, fontSize = 14.sp, lineHeight = 22.sp, color = TextHi)
            }

            Spacer(Modifier.height(28.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(Accent)
                    .clickable {
                        Share.send(context, message, "Invite a friend to VMP")
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Send invite", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(Modifier.height(32.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceHi)
                    .clickable { onPreviewRecipient() }
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        "DEMO ONLY",
                        fontSize = 9.sp,
                        letterSpacing = 2.sp,
                        color = TextLow,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "See what your friend lands on  →",
                        fontSize = 14.sp,
                        color = TextHi
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

/* ======================= 3. RECIPIENT'S FIRST LAUNCH ======================= */

@Composable
private fun WelcomeScreen(onDone: () -> Unit) {
    var added by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Accent),
            contentAlignment = Alignment.Center
        ) {
            Text("V", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
        }

        Spacer(Modifier.height(32.dp))

        Text(
            "$MY_NAME invited you to VMP",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = TextHi
        )

        Spacer(Modifier.height(10.dp))

        Text(
            "Add them now and your first conversation is ready.",
            fontSize = 14.sp,
            color = TextMid
        )

        Spacer(Modifier.height(36.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Surface)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Accent.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    MY_NAME.first().toString(),
                    color = TextHi,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(MY_NAME, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextHi)
                Text("$MY_HANDLE · $MY_NUMBER", fontSize = 12.sp, color = TextLow)
            }
            if (added) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Added",
                    tint = AccentHi,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(if (added) SurfaceHi else Accent)
                .clickable { if (!added) added = true else onDone() }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (added) "Open chat with $MY_NAME" else "Add $MY_NAME",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (added) TextHi else Color.White
            )
        }

        Spacer(Modifier.height(18.dp))

        Text(
            "Skip for now",
            fontSize = 13.sp,
            color = TextLow,
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable { onDone() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}