package com.aokiji.watermarkhelper.models.entities

import java.io.Serializable

data class SummaryItem(val images: MutableList<Image>, val number: Int, val date: String) : Serializable