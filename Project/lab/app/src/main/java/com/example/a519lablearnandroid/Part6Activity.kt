package com.example.a519lablearnandroid

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme

// ─────────────────────────────────────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────────────────────────────────────

class BrowserViewModel : ViewModel() {
    // Hold the URL state that the WebView should display
    var currentUrl by mutableStateOf("https://www.google.com")
        private set

    fun updateUrl(newUrl: String) {
        // Simple auto-prefix for convenience
        val formattedUrl = if (newUrl.startsWith("http://") || newUrl.startsWith("https://")) {
            newUrl
        } else {
            "https://$newUrl"
        }
        currentUrl = formattedUrl
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Activity
// ─────────────────────────────────────────────────────────────────────────────

class Part6Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _519LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BrowserScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Screen & AndroidView Interop
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun BrowserScreen(
    modifier: Modifier = Modifier,
    viewModel: BrowserViewModel = viewModel()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        // Address Bar Area
        AddressBar(
            currentUrl = viewModel.currentUrl,
            onGoClick = { newUrl -> viewModel.updateUrl(newUrl) }
        )

        // The WebView wrapped in Compose
        ComposeWebView(
            url = viewModel.currentUrl,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Takes up remaining screen space
        )
    }
}

@Composable
private fun AddressBar(
    currentUrl: String,
    onGoClick: (String) -> Unit
) {
    var inputText by remember(currentUrl) { mutableStateOf(currentUrl) }
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp)),
            singleLine = true,
            placeholder = { Text("Enter URL") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(
                onGo = {
                    onGoClick(inputText)
                    focusManager.clearFocus()
                }
            ),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = {
                onGoClick(inputText)
                focusManager.clearFocus()
            },
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Go")
        }
    }
}

/**
 * Wraps the classic [android.webkit.WebView] in a Compose [AndroidView].
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ComposeWebView(url: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            // 1. Initialization block: Runs ONCE when the view is created.
            WebView(context).apply {
                // Ensure links open inside the WebView, not the external browser app
                webViewClient = WebViewClient()

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                }
            }
        },
        update = { webView ->
            // 2. Update block: Runs whenever the inputs (like 'url') change.
            // When viewModel.currentUrl changes, this block re-executes.
            webView.loadUrl(url)
        }
    )
}