package by.belchurch.main.android.presentation.ui.last_sermons

import androidx.compose.runtime.Composable
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState

@Composable
fun LastSermonsScreen() {
    val state = rememberWebViewState("https://belchurch.org/archive")
    state.webSettings.apply {
        isJavaScriptEnabled = true
        androidWebSettings.apply {
            isAlgorithmicDarkeningAllowed = true
            safeBrowsingEnabled = true
        }
    }

    WebView(state)
}