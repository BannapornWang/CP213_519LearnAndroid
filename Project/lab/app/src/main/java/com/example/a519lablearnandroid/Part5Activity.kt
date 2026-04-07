package com.example.a519lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// ViewModel  — uses Channel to send ONE-TIME error events (not UI state)
// ─────────────────────────────────────────────────────────────────────────────

class Part5ViewModel : ViewModel() {

    // Channel<String> → each error message is delivered exactly once.
    // Unlike a StateFlow, there's no "current value" that new collectors replay.
    private val _errorChannel = Channel<String>(Channel.BUFFERED)
    val errorFlow = _errorChannel.receiveAsFlow()

    private val errorMessages = listOf(
        "🚨 Network timeout! Please retry.",
        "❌ Server returned 500. Try again later.",
        "📡 No internet connection found.",
        "🔐 Session expired. Please log in again.",
        "💾 Database write failed unexpectedly.",
    )
    private var errorIndex = 0

    /** Simulates an async operation that fails and emits an error event. */
    fun triggerError() {
        viewModelScope.launch {
            val message = errorMessages[errorIndex % errorMessages.size]
            errorIndex++
            _errorChannel.send(message)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Activity
// ─────────────────────────────────────────────────────────────────────────────

class Part5Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _519LabLearnAndroidTheme {
                Part5Screen()
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun Part5Screen(
    viewModel: Part5ViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // ──────────────────────────────────────────────────────────────────────
    // KEY CONCEPT — LaunchedEffect with Unit key:
    //   Launched once when the Composable enters the composition.
    //   Collects the Channel's Flow and, for each error message that arrives,
    //   calls showSnackbar() — a suspending function that queues the message.
    //
    //   WHY NOT UI STATE?
    //   A "showError: String?" State would be re-shown every recomposition
    //   (e.g. on screen rotation) because State retains its value.
    //   A Channel / SharedFlow emits the event once and forgets it — perfect
    //   for one-shot notifications like Snackbars, navigation, or dialogs.
    // ──────────────────────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.errorFlow.collect { errorMessage ->
            snackbarHostState.showSnackbar(message = errorMessage)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF121212),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFFB71C1C),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Illustration ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFFA726),
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Title ─────────────────────────────────────────────────────
            Text(
                text = "Side Effects Demo",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Explanation card ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "The Snackbar is shown via LaunchedEffect that\n" +
                           "collects a Channel<String> from the ViewModel.\n\n" +
                           "Error events are one-shot — they won't replay\n" +
                           "on recomposition, unlike regular UI State.",
                    color = Color(0xFFBDBDBD),
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── Trigger button ────────────────────────────────────────────
            Button(
                onClick = { viewModel.triggerError() },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935),
                    contentColor = Color.White
                ),
                modifier = Modifier.height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Trigger Error",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun Part5Preview() {
    _519LabLearnAndroidTheme {
        Part5Screen()
    }
}