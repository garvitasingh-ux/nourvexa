package com.example.model

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageExportUtil {
    fun exportDietPlanAsImage(context: Context, user: User, plan: DietPlan) {
        val width = 800
        val height = 1200
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw background
        canvas.drawColor(Color.WHITE)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#121212")
            textSize = 48f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        // Draw Header
        canvas.drawText("Nourvexa", width / 2f, 100f, paint)

        paint.apply {
            textSize = 32f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            color = Color.parseColor("#2E7D32")
        }
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val dateString = dateFormat.format(Date())
        canvas.drawText("${user.name}'s Diet Plan - $dateString", width / 2f, 160f, paint)

        // Draw lines and texts
        paint.apply {
            textSize = 24f
            textAlign = Paint.Align.LEFT
            color = Color.parseColor("#121212")
        }

        var yPos = 240f
        val leftMargin = 80f

        canvas.drawText("Calculated for: ${plan.weightSnapshot} kg", leftMargin, yPos, paint)
        yPos += 60f

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText("Food Quantities", leftMargin, yPos, paint)
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        yPos += 40f
        canvas.drawText("• Fruits: ${plan.fruitQuantityGrams} g", leftMargin, yPos, paint)
        yPos += 40f
        canvas.drawText("• Vegetables: ${plan.vegetableQuantityGrams} g", leftMargin, yPos, paint)
        yPos += 40f
        canvas.drawText("• Leafy Greens: ${plan.greensQuantityGrams} g", leftMargin, yPos, paint)
        yPos += 40f
        canvas.drawText("• Snacks: ${plan.snackQuantityGrams} g", leftMargin, yPos, paint)
        yPos += 80f

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText("Daily Timeline", leftMargin, yPos, paint)
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        yPos += 40f

        val timelineItems = listOf(
            "10:00 PM - 6:00 AM : Sleep Window",
            "Morning - 12:00 PM : Seasonal fruits",
            "After 12:00 PM : Lunch Salad + Home-cooked meal",
            "As needed : Snack (Sprouts + Nuts)",
            "By 6:00 PM : Dinner completed",
            "After 8:00 PM : Strict Cutoff - No Food",
            "Daily : 30+ mins Walk + Sunlight"
        )

        for (item in timelineItems) {
            canvas.drawText("• $item", leftMargin, yPos, paint)
            yPos += 40f
        }

        // Save to MediaStore
        saveBitmapToGallery(context, bitmap, "Nourvexa_Plan_${System.currentTimeMillis()}")
    }

    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, title: String) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$title.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val uri = context.contentResolver.insert(collection, values)
        try {
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    context.contentResolver.update(uri, values, null, null)
                }
                Toast.makeText(context, "Saved to Gallery", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }
}
