package com.c242ps518.tanami.helpers

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class CropRecommendationHelper(
    private val context: Context,
    private val modelName: String = "Crop_Recomendation.tflite",
    private val numThreads: Int = 4,
    val recommendationListener: RecommendationListener?
) {
    private var interpreter: Interpreter? = null

    init {
        setupModelInterpreter()
    }

    interface RecommendationListener {
        fun onSuccess(result: String, inferenceTime: Long)
        fun onError(error: String)
    }


    private fun setupModelInterpreter() {
        try {
            val modelFile = loadModelFile()
            val options = Interpreter.Options().apply {
                setNumThreads(numThreads)
            }
            interpreter = Interpreter(modelFile, options)
        } catch (e: Exception) {
            recommendationListener?.onError("Error loading model: ${e.message}")
            Log.e(TAG, "Error loading model", e)
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // Fungsi untuk menjalankan prediksi
    fun predictCrop(inputFeatures: FloatArray) {
        if (interpreter == null) {
            setupModelInterpreter()
        }

        if (interpreter == null) {
            recommendationListener?.onError("Model interpreter is not initialized")
            return
        }

        try {
            val outputArray = Array(1) { FloatArray(LABELS.size) }
            val startTime = System.currentTimeMillis()

            // Run inference
            interpreter?.run(arrayOf(inputFeatures), outputArray)

            val inferenceTime = System.currentTimeMillis() - startTime

            Log.d("TFLite", "Input Data: ${inputFeatures.contentToString()}")
            Log.d("TFLite", "Output Array: ${outputArray[0].contentToString()}")

            // Ambil indeks prediksi dengan skor tertinggi
            Log.d(TAG, "Output Array: ${outputArray.contentDeepToString()}")
            val predictedIndex = outputArray[0].toList().indexOf(outputArray[0].maxOrNull())
            Log.d(TAG, "$predictedIndex")

            // Kirim hasil ke listener
            recommendationListener?.onSuccess(LABELS[predictedIndex], inferenceTime)

        } catch (e: Exception) {
            recommendationListener?.onError("Error during prediction: ${e.message}")
            Log.e(TAG, "Error during prediction", e)
        }
    }

    // Tutup interpreter jika tidak digunakan
    fun close() {
        interpreter?.close()
    }

    companion object {
        const val TAG = "CropRecommendationHelper"

        val LABELS = listOf(
            "rice",         // 1
            "maize",        // 2
            "chickpea",     // 3
            "kidneybeans",  // 4
            "pigeonpeas",   // 5
            "mothbeans",    // 6
            "mungbean",     // 7
            "blackgram",    // 8
            "lentil",       // 9
            "pomegranate",  // 10
            "banana",       // 11
            "mango",        // 12
            "grapes",       // 13
            "watermelon",   // 14
            "muskmelon",    // 15
            "apple",        // 16
            "orange",       // 17
            "papaya",       // 18
            "coconut",      // 19
            "cotton",       // 20
            "jute",         // 21
            "coffee"        // 22
        )
    }

}