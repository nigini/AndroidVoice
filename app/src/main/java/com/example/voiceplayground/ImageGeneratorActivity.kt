package com.example.voiceplayground

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.voiceplayground.ui.theme.VoicePlaygroundTheme

class ImageGeneratorActivity: ComponentActivity() {
    private val openAI = "TODO"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VoicePlaygroundTheme {
                var spokenText = ""
                var imageURL = ""
                val localContext = LocalContext.current
                val coroutineScope = rememberCoroutineScope()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        PromptGenerator(
                            modifier = Modifier,
                            localContext = localContext,
                            prompt = spokenText,
                        ) {
                            //TODO!
                        }
                        ImagePresenter(imageURL = imageURL)
                    }
                }
            }
        }
    }

    @Composable
    fun PromptGenerator(
        modifier: Modifier,
        localContext: Context,
        prompt: String,
        onPromptReady: (String) -> Unit) {

        Column {
            Text(text = prompt)
            Button(
                onClick = {
                    startSpeechInput(
                        context = localContext
                    ) { onPromptReady(it) }
                }
            ){
                Text(text = "Tell me!")
            }
        }
    }

    @Composable
    fun ImagePresenter(imageURL: String){
        val TAG = "PRESENTER"
        Log.d(TAG, imageURL)
        if (imageURL.lowercase().startsWith("http")) {
            //TODO
            Log.d(TAG, "I should generate an Image now!")
        } else {
            var imageID = R.drawable.empty_image_flaticon
            when(imageURL) {
                "ERROR" -> imageID = R.drawable.error_image_flaticon
                "LOADING" -> imageID = R.drawable.processing_image_flaticon
                else -> Log.d(TAG,"Don't recognize this image URL.")
            }
            Image(
                painter = painterResource(id = imageID),
                contentDescription = "Image reflecting the current status of the app."
            )
        }
    }

    suspend fun generateImageFromPrompt(prompt: String): String {
        try {
            //TODO
            return "LOADING"
        } catch (ex: Exception) {
            Log.d("GENERATOR", ex.toString())
            return "ERROR"
        }
    }
}