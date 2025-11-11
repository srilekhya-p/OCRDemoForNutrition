package com.example.ocrdemofornutrition

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import android.graphics.Rect
import android.util.Log
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognizer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView = findViewById<ImageView>(R.id.imageView)
        val btnRecognize = findViewById<Button>(R.id.btnRecognize)
        val textOutput = findViewById<TextView>(R.id.textOutput)

        // Load sample image from drawable
        val OgBitmap = BitmapFactory.decodeResource(resources, R.drawable.sample_bestby_vertical)
        imageView.setImageBitmap(OgBitmap)

        btnRecognize.setOnClickListener {
            // Convert bitmap to ML Kit InputImage
            val image = InputImage.fromBitmap(OgBitmap, 0)

            // Initialize recognizer
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            // Process the image
            //processTextFromImage(recognizer, image, textOutput)
            // Process the image - Text format into blocks & lines
            //ProcessTextIntoBlocksLinesFromImage(recognizer, image)

            //find rectangle of Nutrition facts
            recognizer.process(image)
                .addOnSuccessListener { result ->
                    val tableRect = findNutritionBoundary(result)
                    Log.d("OCR_ALL", "Boundary found $tableRect")
                    if (tableRect != null) {
                        val cropped = cropBitmapToRect(OgBitmap, tableRect)
                        // Convert bitmap to ML Kit InputImage
                        val croppedImage = InputImage.fromBitmap(cropped, 0)
                        imageView.setImageBitmap(cropped)
                        // Re-run ML Kit OCR on `cropped`
                        processTextFromImage(recognizer, croppedImage, textOutput)
                    }
                }



        }
    }



        fun findNutritionBoundary(result: Text): Rect? {
        var startBox: Rect? = null
        var endBox: Rect? = null
        var startPoints: Array<Point>? = null;
        var endPoints: Array<Point>? = null;

                //Log.d("OCR_BON", "All text:\n$resultText")
                for (block in result.textBlocks) {
                    for (line in block.lines) {
                        val text = line.text.lowercase()
                        if (startBox == null && text.contains("nutrition facts")) {
                            startBox = line.boundingBox
                            Log.d("OCR_BON","Text: $text StartBox $startBox")
                            startPoints = line.cornerPoints
                            if (startPoints != null) {
                                for (p in startPoints) {
                                    Log.d("OCR", "start Point: (${p.x}, ${p.y})")
                                }
                            }

                        }
                        if (startBox != null && (text.contains("ingredients") || text.contains("distributed"))) {
                            endBox = line.boundingBox
                            Log.d("OCR_BON","Text : $text EndBox $endBox")
                            endPoints = line.cornerPoints
                            if (endPoints != null) {
                                for (p in endPoints) {
                                    Log.d("OCR", "end Point: (${p.x}, ${p.y})")
                                }
                            }
                        }
                    }
                }
        if(startBox != null && endBox != null ) {
            Log.d("OCR_BON", "Boundary found")
            return buildNutritionTableRect(startPoints!!, endPoints!!)
        }else{
                Log.d("OCR_BON","Boundary not found")
                   return  null
        }

    }

    /**
     * Build a bounding rectangle between two detected text regions
     * using their cornerPoints.
     */
    fun buildNutritionTableRect(startPoints: Array<Point>, endPoints: Array<Point>): Rect {
        // Collect all X and Y from both polygons
        val allX = startPoints.map { it.x } + endPoints.map { it.x }
        val allY = startPoints.map { it.y } + endPoints.map { it.y }

        val left = allX.minOrNull() ?: 0
        val right = allX.maxOrNull() ?: 0
        val top = allY.minOrNull() ?: 0
        val bottom = allY.maxOrNull() ?: 0

        return Rect(left, top, right, bottom)
    }


    /**
     * Crop the bitmap to the given rectangle
     */
    fun cropBitmapToRect(bitmap: Bitmap, rect: Rect): Bitmap {
        // Ensure rect fits inside the bitmap
        val safeRect = Rect(
            rect.left.coerceAtLeast(0),
            rect.top.coerceAtLeast(0),
            rect.right.coerceAtMost(bitmap.width),
            rect.bottom.coerceAtMost(bitmap.height)
        )

        return Bitmap.createBitmap(
            bitmap,
            safeRect.left,
            safeRect.top,
            safeRect.width(),
            safeRect.height()
        )
    }


    private fun ProcessTextIntoBlocksLinesFromImage(
        recognizer: TextRecognizer,
        image: InputImage
    ) {
        // text into block & lines + X, Y co-ordinates
        recognizer.process(image)
            .addOnSuccessListener { result ->
                val resultText = result.text
                Log.d("OCR_ALL", "All text:\n$resultText")
                Log.d("OCR_DEMO", "starts here");

                for (block in result.textBlocks) {
                    for (line in block.lines) {
                        val rect: Rect? = line.boundingBox
                        if (rect != null) {
                            val x = rect.left
                            val y = rect.top
                            val w = rect.width()
                            val h = rect.height()

                            Log.d(
                                "OCR_LINE",
                                "Text: '${line.text}' | X=$x, Y=$y, W=$w, H=$h"
                            )
                        } else {
                            Log.d("OCR_LINE", "Text: '${line.text}' | No bounding box")
                        }
                    }
                }

            }
            .addOnFailureListener { e ->
                Log.e("OCR_ERROR", "Text recognition failed", e)
            }
    }

    private fun processTextFromImage(
        recognizer: TextRecognizer,
        image: InputImage,
        textOutput: TextView
    ) {
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                textOutput.text = visionText.text

            }
            .addOnFailureListener { e ->
                "Error: ${e.message}".also { textOutput.text = it }
                Log.e("OCR_DEMO", "OCR failed", e)
            }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    OCRDemoForNutritionTheme {
//        Greeting("Android")
//    }
//}