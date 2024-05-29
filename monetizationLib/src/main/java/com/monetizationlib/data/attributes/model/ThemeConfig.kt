package com.monetizationlib.data.attributes.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ThemeConfig(
    @SerializedName("mainComponent")
    var mainComponent: MainComponent? = null,
    @SerializedName("buttonComponent")
    var buttonComponent: ButtonComponent? = null,
    @SerializedName("indicatorComponent")
    var stepIndicatorComponent: IndicatorComponent? = null,
) {

    @Keep
    inner class MainComponent {
        var windowBackground: String = "#1F2336"
        var textColorPrimary: String = "#ffffff"
        var textColorSecondary: String = "#DFDFDF"
        var textColorFocus: String = "#F7C865"
        var textColorFocusSecondary: String = "#F57AC0"

        @SerializedName("mainContainer")
        var mainContainerColorConfig: ContainerConfig = ContainerConfig()

        @SerializedName("secondaryContainer")
        var secondaryContainerColorConfig: ContainerConfig = ContainerConfig().secondaryContainer()
    }

    @Keep
    inner class ButtonComponent {
        @SerializedName("primary")
        var primary: ContainerConfig? = ContainerConfig().defaultPrimaryButton()

        @SerializedName("secodary")
        var secodary: ContainerConfig? = ContainerConfig().defaultSecondaryButton()
    }

    @Keep
    inner class IndicatorComponent {
        var stepIndicatorStartColor: String = "#893060"
        var stepIndicatorEndColor: String = "#72244b"
        var stepIndicatorBorderStartColor: String = "#ad4c82"
        var stepIndicatorBorderEndColor: String = "#5F1D3D"
        var defaultIndicatorProgressStartColor: String = "#95386B"
        var defaultIndicatorProgressEndColor: String = "#95386B"
        var selectedIndicatorProgressStartColor: String = "#95386B"
        var selectedIndicatorProgressEndColor: String = "#95386B"
        var selectedStepCircleStartColor: String = "#FFB959"
        var selectedStepCircleEndColor: String = "#C77E1A"
        var selectedStepCircleStartBorderColor: String = "#FFBD62"
        var selectedStepCircleEndBorderColor: String = "#AC6D16"
        var defaultStepCircleStartColor: String = "#FFFFFF"
        var defaultStepCircleEndColor: String = "#F4F4F4"
        var defaultStepCircleStartBorderColor: String = "#FAFAFA"
        var defaultStepCircleEndBorderColor: String = "#D0D0D0"
    }
}

@Keep
data class ContainerConfig(
    var startColor: String = "#C64C95",
    var endColor: String = "#6D2347",

    @SerializedName("startBorderColor", alternate = ["borderStartColor"])
    var startBorderColor: String = "#943261",
    @SerializedName("endBorderColor", alternate = ["borderEndColor"])
    var endBorderColor: String = "#4C1631",
) {

    fun secondaryContainer(): ContainerConfig {
        this.startColor = "#792851"
        this.endColor = "#792851"
        this.startBorderColor = "#943261"
        this.endBorderColor = "#671B41"
        return this
    }

    fun defaultPrimaryButton(): ContainerConfig {
        this.startColor = "#FEB857"
        this.endColor = "#E9992C"
        this.startBorderColor = "#FFDBA9"
        this.endBorderColor = "#996F35"
        return this
    }

    fun defaultSecondaryButton(): ContainerConfig {
        this.startColor = "#7E2250"
        this.endColor = "#6D1C45"
        this.startBorderColor = "#A3366C"
        this.endBorderColor = "#431029"
        return this
    }
}
