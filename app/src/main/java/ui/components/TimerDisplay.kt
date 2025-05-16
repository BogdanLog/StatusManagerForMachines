package ui.components

import android.annotation.SuppressLint
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@SuppressLint("DefaultLocale")
@Composable
fun TimerDisplay(seconds: Long) {
    val formattedTime = remember(seconds) {
        String.format(
            "%02d:%02d:%02d",
            seconds / 3600,
            (seconds % 3600) / 60,
            seconds % 60
        )
    }
    Text(text = "Таймер: $formattedTime", style = MaterialTheme.typography.displaySmall)
}