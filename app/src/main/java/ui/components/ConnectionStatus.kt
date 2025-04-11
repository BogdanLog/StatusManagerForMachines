package ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ConnectionStatus(state: ConnectionState) {
    val (text, color) = when (state) {
        ConnectionState.Connected -> "Подключено" to Color.Green
        ConnectionState.Connecting -> "Подключение..." to Color.Yellow
        ConnectionState.Disconnected -> "Отключено" to Color.Red
    }

    Text(text = text, color = color)
}