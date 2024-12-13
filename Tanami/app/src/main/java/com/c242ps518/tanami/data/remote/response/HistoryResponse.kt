package com.c242ps518.tanami.data.remote.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class HistoryResponse(

	@field:SerializedName("data")
	val dataItem: List<DataItem> = emptyList(),

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class InputData(

	@field:SerializedName("rainfall")
	val rainfall: Double? = null,

	@field:SerializedName("inputData")
	val inputData: List<String> = emptyList(),

	@field:SerializedName("imagePath")
	val imagePath: String? = null,

	@field:SerializedName("humidity")
	val humidity: Double? = null,

	@field:SerializedName("P")
	val p: Double? = null,

	@field:SerializedName("ph")
	val ph: Double? = null,

	@field:SerializedName("temperature")
	val temperature: Double? = null,

	@field:SerializedName("K")
	val k: Double? = null,

	@field:SerializedName("N")
	val n: Double? = null
) : Parcelable

//@Parcelize
//data class PlantDetails(
//
//	@field:SerializedName("cultivation_duration")
//	val cultivationDuration: CultivationDuration? = null,
//
//	@field:SerializedName("additional_tips")
//	val additionalTips: List<String?>? = null,
//
//	@field:SerializedName("description")
//	val description: String? = null,
//
//	@field:SerializedName("shopping_list")
//	val shoppingList: List<ShoppingListItem> = emptyList(),
//
//	@field:SerializedName("care_instructions")
//	val careInstructions: CareInstructions? = null,
//
//	@field:SerializedName("recommended_soil")
//	val recommendedSoil: RecommendedSoil? = null
//) : Parcelable

//@Parcelize
//data class CultivationDuration(
//
//	@field:SerializedName("optimal_temperature")
//	val optimalTemperature: String? = null,
//
//	@field:SerializedName("best_planting_season")
//	val bestPlantingSeason: String? = null,
//
//	@field:SerializedName("seed_to_harvest")
//	val seedToHarvest: String? = null
//) : Parcelable

//@Parcelize
//data class ShoppingListItem(
//
//	@field:SerializedName("item")
//	val item: String? = null,
//
//	@field:SerializedName("purchase_link")
//	val purchaseLink: String? = null,
//
//	@field:SerializedName("tips")
//	val tips: String? = null
//) : Parcelable

//@Parcelize
//data class CareInstructions(
//
//	@field:SerializedName("steps")
//	val steps: List<String> = emptyList()
//) : Parcelable

//@Parcelize
//data class RecommendedSoil(
//
//	@field:SerializedName("organic_content")
//	val organicContent: String? = null,
//
//	@field:SerializedName("ph")
//	val ph: String? = null,
//
//	@field:SerializedName("drainage")
//	val drainage: String? = null,
//
//	@field:SerializedName("type")
//	val type: String? = null
//) : Parcelable

@Parcelize
data class DataItem(

	@field:SerializedName("PredictionDate")
	val predictionDate: String? = null,

	@field:SerializedName("UserID")
	val userID: Int? = null,

	@field:SerializedName("PredictionID")
	val predictionID: Int? = null,

	@field:SerializedName("Label")
	val label: String? = null,

	@field:SerializedName("PlantPhoto")
	val plantPhoto: String? = null,

	@field:SerializedName("Input_Data")
	val inputData: InputData? = null,

	@field:SerializedName("plantDetails")
	val plantDetails: PlantDetails? = null
) : Parcelable
