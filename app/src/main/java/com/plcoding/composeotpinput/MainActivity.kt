package com.plcoding.composeotpinput

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plcoding.composeotpinput.ui.theme.ComposeOTPInputTheme
import com.plcoding.composeotpinput.ui.theme.PLCodingGray

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeOTPInputTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = PLCodingGray
                ) { innerPadding ->
                    val viewModel = viewModel<OtpViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    val focusRequesters = remember {
                        List(4) { FocusRequester() }
                    }
                    val focusManager = LocalFocusManager.current
                    val keyboardManager = LocalSoftwareKeyboardController.current

                    LaunchedEffect(state.focusedIndex) {
                        state.focusedIndex?.let { index ->
                            focusRequesters.getOrNull(index)?.requestFocus()
                        }
                    }

                    LaunchedEffect(state.code, keyboardManager) {
                        val allNumbersEntered = state.code.none { it == null }
                        if(allNumbersEntered) {
                            focusRequesters.forEach {
                                it.freeFocus()
                            }
                            focusManager.clearFocus()
                            keyboardManager?.hide()
                        }
                    }

                    OtpScreen(
                        state = state,
                        focusRequesters = focusRequesters,
                        onAction = { action ->
                            when(action) {
                                is OtpAction.OnEnterNumber -> {
                                    if(action.number != null) {
                                        focusRequesters[action.index].freeFocus()
                                    }
                                }
                                else -> Unit
                            }
                            viewModel.onAction(action)
                        },
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeOTPInputTheme {
        Greeting("Android")
    }
}