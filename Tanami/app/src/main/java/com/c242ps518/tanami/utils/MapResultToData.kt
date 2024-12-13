package com.c242ps518.tanami.utils

import org.json.JSONObject

object MapResultToData {
    fun mapResult(predictedLabel: String): Map<String, Any>? {
        // JSON data
        val jsonData = """
        {
  "alluvial": {"nitrogen": 50, "phosphorus": 75, "potassium": 120, "pH": 6.5},
  "black": {"nitrogen": 100, "phosphorus": 150, "potassium": 180, "pH": 7.2},
  "cinder": {"nitrogen": 20, "phosphorus": 40, "potassium": 60, "pH": 5.5},
  "clay": {"nitrogen": 75, "phosphorus": 100, "potassium": 150, "pH": 6.0},
  "laterite": {"nitrogen": 30, "phosphorus": 55, "potassium": 80, "pH": 4.5},
  "loamy": {"nitrogen": 90, "phosphorus": 120, "potassium": 160, "pH": 6.8},
  "peat": {"nitrogen": 120, "phosphorus": 80, "potassium": 100, "pH": 4.0},
  "red": {"nitrogen": 60, "phosphorus": 70, "potassium": 130, "pH": 5.5},
  "sandy": {"nitrogen": 25, "phosphorus": 45, "potassium": 70, "pH": 6.0},
  "sandy_loam": {"nitrogen": 50, "phosphorus": 65, "potassium": 110, "pH": 6.5},
  "yellow": {"nitrogen": 40, "phosphorus": 55, "potassium": 90, "pH": 5.0}
}
    """.trimIndent()

        // Parse JSON into JSONObject
        val jsonObject = JSONObject(jsonData)

        // Check if the predicted label exists in JSON
        return if (jsonObject.has(predictedLabel)) {
            // Convert the selected JSON object to a Map
            val selectedData = jsonObject.getJSONObject(predictedLabel)
            selectedData.keys().asSequence().associateWith { selectedData.get(it) }
        } else {
            null // Return null if label not found
        }
    }
}