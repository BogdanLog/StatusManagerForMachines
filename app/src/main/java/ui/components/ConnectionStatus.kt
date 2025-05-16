package ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.statusmanagerformachines.R

@Composable
fun ConnectionStatus(state: ConnectionState) {
    val iconRes = when (state) {
        ConnectionState.Connected -> R.drawable.ic_connected_connecting_cloud
        ConnectionState.Connecting -> R.drawable.ic_connected_connecting_cloud
        ConnectionState.Disconnected -> R.drawable.ic_disconnected_cloud
    }
    val tint = when (state) {
        ConnectionState.Connected -> Color.Green
        ConnectionState.Connecting -> Color.Yellow
        ConnectionState.Disconnected -> Color.Red
    }
    Icon(
        painter = painterResource(id = iconRes),
        contentDescription = when (state) {
            ConnectionState.Connected -> "Подключено"
            ConnectionState.Connecting -> "Подключение..."
            ConnectionState.Disconnected -> "Отключено"
        },
        tint = tint,
        modifier = Modifier
            .size(64.dp)
            .padding(start = 15.dp)
    )
}