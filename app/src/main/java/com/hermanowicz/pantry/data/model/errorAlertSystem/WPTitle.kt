package com.hermanowicz.pantry.data.model.errorAlertSystem

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WPTitle(
    @SerializedName("rendered")
    @Expose
    var rendered: String? = null
)