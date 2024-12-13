package com.c242ps518.tanami.data.remote.response

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class FieldResponse(

	@field:SerializedName("prediction")
	val prediction: String? = null,

	@field:SerializedName("predictionId")
	val predictionId: Int? = null,

	@field:SerializedName("plantDetails")
	val plantDetails: PlantDetails? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class PlantDetails(

	@field:SerializedName("cultivation_duration")
	val cultivationDuration: CultivationDuration? = null,

	@field:SerializedName("additional_tips")
	val additionalTips: List<String?>? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("shopping_list")
	val shoppingList: List<ShoppingListItem?>? = null,

	@field:SerializedName("care_instructions")
	val careInstructions: CareInstructions? = null,

	@field:SerializedName("recommended_soil")
	val recommendedSoil: RecommendedSoil? = null
) : Parcelable

@Parcelize
data class RecommendedSoil(

	@field:SerializedName("organic_content")
	val organicContent: String? = null,

	@field:SerializedName("ph")
	val ph: String? = null,

	@field:SerializedName("drainage")
	val drainage: String? = null,

	@field:SerializedName("type")
	val type: String? = null
) : Parcelable

@Parcelize
data class ShoppingListItem(

	@field:SerializedName("item")
	val item: String? = null,

	@field:SerializedName("purchase_link")
	val purchaseLink: String? = null,

	@field:SerializedName("tips")
	val tips: String? = null
) : Parcelable

@Parcelize
data class CareInstructions(

	@field:SerializedName("steps")
	val steps: List<String?>? = null
) : Parcelable

@Parcelize
data class CultivationDuration(

	@field:SerializedName("optimal_temperature")
	val optimalTemperature: String? = null,

	@field:SerializedName("best_planting_season")
	val bestPlantingSeason: String? = null,

	@field:SerializedName("seed_to_harvest")
	val seedToHarvest: String? = null
) : Parcelable
