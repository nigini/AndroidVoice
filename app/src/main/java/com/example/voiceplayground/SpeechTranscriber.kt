package com.example.voiceplayground

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale


class RecListener( onTranscription: (String)->Unit): RecognitionListener {
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