package com.monetizationlib.data.base.view.utility

public enum class StepProcessingError(var message:String) {
    APP_NOT_INSTALLED(""),
    AD_NOT_AVAILABLE(""),
    AD_LIMIT_REACHED(""),
    API_ERROR("Failed to process step, please close and retry again"),
}