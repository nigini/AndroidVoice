package com.example.voiceplayground

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
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
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.voiceplayground.ui.theme.VoicePlaygroundTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.handleIntent()
    }

    private fun Intent.handleIntent() {
        val TAG = "INTENT"
        when (action) {
            Intent.ACTION_VIEW -> {
                Log.d(TAG, "==== TRIGGERED ====")
                Log.d(TAG, intent.toString())
            }
            else -> startActivity(intent)
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

class RecListener( onTranscription: (String)->Unit): RecognitionListener{
    val onTranscription = onTranscription
    val TAG = "LISTENING"

    override fun onReadyForSpeech(params: Bundle?) {
        Log.d(TAG, "===== onReadyForSpeech ====")
    }

    override fun onBeginningOfSpeech() {
        Log.d(TAG, "===== onBeginningOfSpeech ====")
    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.d(TAG, "===== onRmsChanged ====")
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        Log.d(TAG, "===== onBufferReceived ====")
    }

    override fun onEndOfSpeech() {
        Log.d(TAG, "===== onEndOfSpeech ====")
    }

    override fun onError(error: Int) {
        Log.d(TAG, "===== onError $error ====")
    }

    override fun onResults(results: Bundle?) {
        var result = ""
        val transcription = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if(transcription != null) {
            for( t in transcription ) {
                result += t+"\n"
            }
        }
        onTranscription(result)
    }

    override fun onPartialResults(partialResults: Bundle?) {
        Log.d(TAG, "===== onPartialResults ====")
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.d(TAG, "===== onEvent ====")
    }
}

fun startSpeechInput(context: Context, onResult: (String) -> Unit) {
    val speech = SpeechRecognizer.createSpeechRecognizer(context)
    speech.setRecognitionListener(RecListener(onResult))
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    speech.startListening(intent)
}