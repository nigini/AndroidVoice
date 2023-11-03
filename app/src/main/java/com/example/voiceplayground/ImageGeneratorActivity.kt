package com.example.voiceplayground

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.client.OpenAI
import com.example.voiceplayground.ui.theme.VoicePlaygroundTheme
import kotlinx.coroutines.launch
import java.util.Properties
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.seconds

class ImageGeneratorActivity: ComponentActivity() {
    private val openAI = OpenAI(
        token = "YOUR_OPENAI_API_KEY",
        timeout = Timeout(socket = 30.seconds)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VoicePlaygroundTheme {
                var spokenText by rememberSaveable { mutableStateOf("What do you want to see next?") }
                var imageURL by rememberSaveable { mutableStateOf("") }
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
                            spokenText = it
                            imageURL = "LOADING"
                            coroutineScope.launch {
                                imageURL = generateImageFromPrompt(it)
                            }
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
            AsyncImage(
                model = imageURL,
                contentDescription = null,
            )
        } else {
            var image_id = R.drawable.empty_image_flaticon
            when(imageURL) {
                "ERROR" -> image_id = R.drawable.error_image_flaticon
                "LOADING" -> image_id = R.drawable.processing_image_flaticon
                else -> Log.d(TAG,"Don't recognize this image URL.")
            }
            Image(
                painter = painterResource(id = image_id),
                contentDescription = "Image reflecting the current status of the app."
            )
        }
    }

    suspend fun generateImageFromPrompt(prompt: String): String {
        try {
            val image = openAI.imageURL(
                creation = ImageCreation(
                    prompt = prompt,
                    n = 1,
                    size = ImageSize.is1024x1024
                )
            )
            return image[0].url
        } catch (ex: Exception) {
            Log.d("GENERATOR", ex.toString())
            return "ERROR"
        }
    }
}