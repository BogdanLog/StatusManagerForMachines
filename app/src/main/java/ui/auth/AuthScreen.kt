package ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(
    vm: AuthViewModel,
    onDone: () -> Unit
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthState.Authenticated) {
            onDone()
        }
    }

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            AuthState.Loading -> {
                CircularProgressIndicator()
            }

            else -> {
                val login by vm.loginText.collectAsState()
                val pass by vm.passText.collectAsState()

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp)
                ) {
                    Text("Вход", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = login,
                        onValueChange = vm::onLoginChange,
                        label = { Text("Логин") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pass,
                        onValueChange = vm::onPassChange,
                        label = { Text("Пароль") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = vm::attemptLogin,
                        enabled = login.isNotBlank() && pass.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Войти")
                    }
                    if (state is AuthState.Error) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Неправильный логин или пароль",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}