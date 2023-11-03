package com.example.voiceplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.voiceplayground.ui.theme.VoicePlaygroundTheme

class NoteTakerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VoicePlaygroundTheme {
                var spokenText by rememberSaveable { mutableStateOf("") }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NoteTaker(text = spokenText, onTextChange =  {spokenText = it})
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteTaker(text: String, onTextChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val localContext = LocalContext.current
    Column {
        TextField(
            value = text,
            onValueChange = {onTextChange(it)}
        )
        Button(
            onClick = {
                startSpeechInput(
                    context = localContext
                ) { onTextChange(text + it + "\n") }
            }
        ){
            Text(text = "Speak")
        }
    }

}