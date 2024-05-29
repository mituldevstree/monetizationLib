package com.monetizationlib.data.attributes.model

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.monetizationlib.data.R
import com.monetizationlib.data.base.extensions.isValidColor
import com.monetizationlib.data.base.extensions.toJson


@Keep
object ThemeConfigBuilder {
    private lateinit var validThemeConfig: ThemeConfig

    @SuppressLint("ResourceType")
    fun buildWithRemote(context: Context, remoteConfig: ThemeConfig?): ThemeConfigBuilder {
        val themeConfig = ThemeConfig()
        Log.wtf("ThemeConfig", themeConfig.toJson())
        if (remoteConfig?.mainComponent == null) {
            themeConfig.mainComponent = themeConfig.MainComponent()
        } else {
            themeConfig.mainComponent = remoteConfig.mainComponent
            if (remoteConfig.mainComponent?.windowBackground?.isValidColor() == false) {
                themeConfig.mainComponent?.windowBackground =
                    context.resources.getString(R.color.windowBackground)
            } else {
                themeConfig.mainComponent?.windowBackground =
                    remoteConfig.mainComponent?.windowBackground
                        ?: context.resources.getString(R.color.windowBackground)
            }

            if (remoteConfig.mainComponent?.textColorPrimary?.isValidColor() == false) {
                themeConfig.mainComponent?.textColorPrimary =
                    context.resources.getString(R.color.textColorLibPrimary)
            } else {
                themeConfig.mainComponent?.textColorPrimary =
                    remoteConfig.mainComponent?.textColorPrimary
                        ?: context.resources.getString(R.color.textColorLibPrimary)
            }

            if (remoteConfig.mainComponent?.textColorSecondary?.isValidColor() == false) {
                themeConfig.mainComponent?.textColorSecondary =
                    context.resources.getString(R.color.textColorLibSecondary)
            } else {
                themeConfig.mainComponent?.textColorSecondary =
                    remoteConfig.mainComponent?.textColorSecondary
                        ?: context.resources.getString(R.color.textColorLibSecondary)
            }

            if (remoteConfig.mainComponent?.textColorFocus?.isValidColor() == false) {
                themeConfig.mainComponent?.textColorFocus =
                    context.resources.getString(R.color.textColorLibFocus)
            } else {
                themeConfig.mainComponent?.textColorFocus =
                    remoteConfig.mainComponent?.textColorFocus
                        ?: context.resources.getString(R.color.textColorLibFocus)
            }

            if (remoteConfig.mainComponent?.textColorFocusSecondary?.isValidColor() == false) {
                themeConfig.mainComponent?.textColorFocusSecondary =
                    context.resources.getString(R.color.textColorLibFocusSecondary)
            } else {
                themeConfig.mainComponent?.textColorFocusSecondary =
                    remoteConfig.mainComponent?.textColorFocusSecondary
                        ?: context.resources.getString(R.color.textColorLibFocusSecondary)
            }


            if (remoteConfig.mainComponent?.mainContainerColorConfig != null) {
                themeConfig.mainComponent?.mainContainerColorConfig =
                    remoteConfig.mainComponent?.mainContainerColorConfig!!

                if (remoteConfig.mainComponent?.mainContainerColorConfig?.startColor?.isValidColor() == false) {
                    themeConfig.mainComponent?.mainContainerColorConfig?.startColor =
                        context.resources.getString(R.color.mainContainerStartColor)
                } else {
                    themeConfig.mainComponent?.mainContainerColorConfig?.startColor =
                        remoteConfig.mainComponent?.mainContainerColorConfig?.startColor
                            ?: context.resources.getString(R.color.mainContainerStartColor)
                }
                if (remoteConfig.mainComponent?.mainContainerColorConfig?.endColor?.isValidColor() == false) {
                    themeConfig.mainComponent?.mainContainerColorConfig?.endColor =
                        context.resources.getString(R.color.mainContainerEndColor)
                } else {
                    themeConfig.mainComponent?.mainContainerColorConfig?.endColor =
                        remoteConfig.mainComponent?.mainContainerColorConfig?.endColor
                            ?: context.resources.getString(R.color.mainContainerEndColor)
                }

                if (remoteConfig.mainComponent?.mainContainerColorConfig?.startBorderColor?.isValidColor() == false) {
                    themeConfig.mainComponent?.mainContainerColorConfig?.startBorderColor =
                        context.resources.getString(R.color.mainContainerBorderStartColor)
                } else {
                    themeConfig.mainComponent?.mainContainerColorConfig?.startBorderColor =
                        remoteConfig.mainComponent?.mainContainerColorConfig?.startBorderColor
                            ?: context.resources.getString(R.color.mainContainerBorderStartColor)
                }

                if (remoteConfig.mainComponent?.mainContainerColorConfig?.endBorderColor?.isValidColor() == false) {
                    themeConfig.mainComponent?.mainContainerColorConfig?.endBorderColor =
                        context.resources.getString(R.color.mainContainerBorderEndColor)
                } else {
                    themeConfig.mainComponent?.mainContainerColorConfig?.endBorderColor =
                        remoteConfig.mainComponent?.mainContainerColorConfig?.endBorderColor
                            ?: context.resources.getString(R.color.mainContainerBorderEndColor)
                }


            } else {
                themeConfig.mainComponent?.mainContainerColorConfig =
                    ContainerConfig()
            }


            if (remoteConfig.mainComponent?.secondaryContainerColorConfig != null) {
                themeConfig.mainComponent?.secondaryContainerColorConfig =
                    remoteConfig.mainComponent?.secondaryContainerColorConfig!!

                if (remoteConfig.mainComponent?.secondaryContainerColorConfig?.startColor?.isValidColor() == false) {
                    themeConfig.mainComponent?.secondaryContainerColorConfig?.startColor =
                        context.resources.getString(R.color.secondaryContainerStartColor)
                } else {
                    themeConfig.mainComponent?.secondaryContainerColorConfig?.startColor =
                        remoteConfig.mainComponent?.secondaryContainerColorConfig?.startColor
                            ?: context.resources.getString(R.color.secondaryContainerStartColor)
                }
                if (remoteConfig.mainComponent?.secondaryContainerColorConfig?.endColor?.isValidColor() == false) {
                    themeConfig.mainComponent?.secondaryContainerColorConfig?.endColor =
                        context.resources.getString(R.color.secondaryContainerEndColor)
                } else {
                    themeConfig.mainComponent?.secondaryContainerColorConfig?.endColor =
                        remoteConfig.mainComponent?.secondaryContainerColorConfig?.endColor
                            ?: context.resources.getString(R.color.secondaryContainerEndColor)
                }

                if (remoteConfig.mainComponent?.secondaryContainerColorConfig?.startBorderColor?.isValidColor() == false) {
                    themeConfig.mainComponent?.secondaryContainerColorConfig?.startBorderColor =
                        context.resources.getString(R.color.secondaryContainerBorderStartColor)
                } else {
                    themeConfig.mainComponent?.secondaryContainerColorConfig?.startBorderColor =
                        remoteConfig.mainComponent?.secondaryContainerColorConfig?.startBorderColor
                            ?: context.resources.getString(R.color.secondaryContainerBorderStartColor)
                }

                if (remoteConfig.mainComponent?.secondaryContainerColorConfig?.endBorderColor?.isValidColor() == false) {
                    themeConfig.mainComponent?.secondaryContainerColorConfig?.endBorderColor =
                        context.resources.getString(R.color.secondaryContainerBorderEndColor)
                } else {
                    themeConfig.mainComponent?.secondaryContainerColorConfig?.endBorderColor =
                        remoteConfig.mainComponent?.secondaryContainerColorConfig?.endBorderColor
                            ?: context.resources.getString(R.color.secondaryContainerBorderEndColor)
                }

            } else {
                themeConfig.mainComponent?.secondaryContainerColorConfig =
                    ContainerConfig().secondaryContainer()
            }

        }
        if (remoteConfig?.buttonComponent == null) {
            themeConfig.buttonComponent = themeConfig.ButtonComponent()
        } else {
            themeConfig.buttonComponent = remoteConfig.buttonComponent
            if (remoteConfig.buttonComponent?.primary != null) {
                themeConfig.buttonComponent?.primary = remoteConfig.buttonComponent?.primary

                if (themeConfig.buttonComponent?.primary?.startColor?.isValidColor() == true) {
                    themeConfig.buttonComponent?.primary?.startColor =
                        remoteConfig.buttonComponent?.primary?.startColor
                            ?: context.resources.getString(R.color.primaryButtonStartColor)

                } else {
                    themeConfig.buttonComponent?.primary?.startColor =
                        context.resources.getString(R.color.primaryButtonStartColor)
                }

                if (themeConfig.buttonComponent?.primary?.endColor?.isValidColor() == true) {
                    themeConfig.buttonComponent?.primary?.endColor =
                        remoteConfig.buttonComponent?.primary?.endColor
                            ?: context.resources.getString(R.color.primaryButtonEndColor)

                } else {
                    themeConfig.buttonComponent?.primary?.endColor =
                        context.resources.getString(R.color.primaryButtonEndColor)
                }

                if (themeConfig.buttonComponent?.primary?.startBorderColor?.isValidColor() == true) {
                    themeConfig.buttonComponent?.primary?.startBorderColor =
                        remoteConfig.buttonComponent?.primary?.startBorderColor
                            ?: context.resources.getString(R.color.primaryButtonBorderStartColor)

                } else {
                    themeConfig.buttonComponent?.primary?.startBorderColor =
                        context.resources.getString(R.color.primaryButtonBorderStartColor)
                }

                if (themeConfig.buttonComponent?.primary?.endBorderColor?.isValidColor() == true) {
                    themeConfig.buttonComponent?.primary?.endBorderColor =
                        remoteConfig.buttonComponent?.primary?.endBorderColor
                            ?: context.resources.getString(R.color.primaryButtonBorderEndColor)

                } else {
                    themeConfig.buttonComponent?.primary?.endBorderColor =
                        context.resources.getString(R.color.primaryButtonBorderEndColor)
                }
            } else {
                themeConfig.buttonComponent?.primary =
                    ContainerConfig().defaultPrimaryButton()

            }
            if (remoteConfig.buttonComponent?.secodary != null) {
                themeConfig.buttonComponent?.secodary =
                    remoteConfig.buttonComponent?.secodary

                if (themeConfig.buttonComponent?.secodary?.startColor?.isValidColor() == true) {
                    themeConfig.buttonComponent?.secodary?.startColor =
                        remoteConfig.buttonComponent?.secodary?.startColor
                            ?: context.resources.getString(R.color.secondaryButtonStartColor)

                } else {
                    themeConfig.buttonComponent?.secodary?.startColor =
                        context.resources.getString(R.color.secondaryButtonStartColor)
                }

                if (themeConfig.buttonComponent?.secodary?.endColor?.isValidColor() == true) {
                    themeConfig.buttonComponent?.secodary?.endColor =
                        remoteConfig.buttonComponent?.secodary?.endColor
                            ?: context.resources.getString(R.color.secondaryButtonEndColor)

                } else {
                    themeConfig.buttonComponent?.secodary?.endColor =
                        context.resources.getString(R.color.secondaryButtonEndColor)
                }

                if (themeConfig.buttonComponent?.secodary?.startBorderColor?.isValidColor() == true) {
                    themeConfig.buttonComponent?.secodary?.startBorderColor =
                        remoteConfig.buttonComponent?.secodary?.startBorderColor
                            ?: context.resources.getString(R.color.secondaryButtonBorderStartColor)

                } else {
                    themeConfig.buttonComponent?.secodary?.startBorderColor =
                        context.resources.getString(R.color.secondaryButtonBorderStartColor)
                }

                if (themeConfig.buttonComponent?.secodary?.endBorderColor?.isValidColor() == true) {
                    themeConfig.buttonComponent?.secodary?.endBorderColor =
                        remoteConfig.buttonComponent?.secodary?.endBorderColor
                            ?: context.resources.getString(R.color.secondaryButtonBorderEndColor)

                } else {
                    themeConfig.buttonComponent?.secodary?.endBorderColor =
                        context.resources.getString(R.color.secondaryButtonBorderEndColor)
                }

            } else {
                themeConfig.buttonComponent?.secodary =
                    ContainerConfig().defaultSecondaryButton()

            }
        }

        if (remoteConfig?.stepIndicatorComponent == null) {
            themeConfig.stepIndicatorComponent = themeConfig.IndicatorComponent()
        } else {
            themeConfig.stepIndicatorComponent = remoteConfig.stepIndicatorComponent
            if (remoteConfig.stepIndicatorComponent?.stepIndicatorStartColor?.isValidColor() == true) {
                themeConfig.stepIndicatorComponent?.stepIndicatorStartColor =
                    remoteConfig.stepIndicatorComponent?.stepIndicatorStartColor
                        ?: context.resources.getString(R.color.stepIndicatorStartColor)
            } else {
                context.resources.getString(R.color.stepIndicatorStartColor)
            }

            if (remoteConfig.stepIndicatorComponent?.stepIndicatorEndColor?.isValidColor() == true) {
                themeConfig.stepIndicatorComponent?.stepIndicatorEndColor =
                    remoteConfig.stepIndicatorComponent?.stepIndicatorEndColor
                        ?: context.resources.getString(R.color.stepIndicatorEndColor)
            } else {
                themeConfig.stepIndicatorComponent?.stepIndicatorEndColor =
                    context.resources.getString(R.color.stepIndicatorEndColor)
            }

            if (remoteConfig.stepIndicatorComponent?.stepIndicatorBorderStartColor?.isValidColor() == true) {
                themeConfig.stepIndicatorComponent?.stepIndicatorBorderStartColor =
                    remoteConfig.stepIndicatorComponent?.stepIndicatorBorderStartColor
                        ?: context.resources.getString(R.color.stepIndicatorBorderStartColor)
            } else {
                themeConfig.stepIndicatorComponent?.stepIndicatorBorderStartColor =
                    context.resources.getString(R.color.stepIndicatorBorderStartColor)
            }
            if (remoteConfig.stepIndicatorComponent?.stepIndicatorBorderEndColor?.isValidColor() == true) {
                themeConfig.stepIndicatorComponent?.stepIndicatorBorderEndColor =
                    remoteConfig.stepIndicatorComponent?.stepIndicatorBorderEndColor
                        ?: context.resources.getString(R.color.stepIndicatorBorderEndColor)
            } else {
                themeConfig.stepIndicatorComponent?.stepIndicatorBorderEndColor =
                    context.resources.getString(R.color.stepIndicatorBorderEndColor)
            }
            if (remoteConfig.stepIndicatorComponent?.defaultIndicatorProgressStartColor?.isValidColor() == true) {
                themeConfig.stepIndicatorComponent?.defaultIndicatorProgressStartColor =
                    remoteConfig.stepIndicatorComponent?.defaultIndicatorProgressStartColor
                        ?: context.resources.getString(R.color.defaultIndicatorStartColor)
            } else {
                themeConfig.stepIndicatorComponent?.defaultIndicatorProgressStartColor =
                    context.resources.getString(R.color.defaultIndicatorStartColor)
            }
            if (remoteConfig.stepIndicatorComponent?.defaultIndicatorProgressEndColor?.isValidColor() == true) {
                themeConfig.stepIndicatorComponent?.defaultIndicatorProgressEndColor =
                    remoteConfig.stepIndicatorComponent?.defaultIndicatorProgressEndColor
                        ?: context.resources.getString(R.color.defaultIndicatorEndColor)
            } else {
                themeConfig.stepIndicatorComponent?.defaultIndicatorProgressEndColor =
                    context.resources.getString(R.color.defaultIndicatorEndColor)
            }
            if (remoteConfig.stepIndicatorComponent?.selectedIndicatorProgressStartColor?.isValidColor() == true) {
                themeConfig.stepIndicatorComponent?.selectedIndicatorProgressStartColor =
                    remoteConfig.stepIndicatorComponent?.selectedIndicatorProgressStartColor
                        ?: context.resources.getString(R.color.selectedIndicatorStartColor)
            } else {
                themeConfig.stepIndicatorComponent?.selectedIndicatorProgressStartColor =
                    context.resources.getString(R.color.selectedIndicatorStartColor)
            }

            if (remoteConfig.stepIndicatorComponent?.selectedIndicatorProgressEndColor?.isValidColor() == true) {
                themeConfig.stepIndicatorComponent?.selectedIndicatorProgressEndColor =
                    remoteConfig.stepIndicatorComponent?.selectedIndicatorProgressEndColor
                        ?: context.resources.getString(R.color.selectedIndicatorEndColor)
            } else {
                themeConfig.stepIndicatorComponent?.selectedIndicatorProgressEndColor =
                    context.resources.getString(R.color.selectedIndicatorEndColor)
            }


            if (remoteConfig.stepIndicatorComponent?.defaultStepCircleStartColor?.isValidColor() == true) {
                themeConfig.stepIndicatorComponent?.defaultStepCircleStartColor =
                    remoteConfig.stepIndicatorComponent?.defaultStepCircleStartColor
                        ?: context.resources.getString(R.color.selectedIndicatorEndColor)
            } else {
                themeConfig.stepIndicatorComponent?.defaultStepCircleStartColor =
                    context.resources.getString(R.color.selectedIndicatorEndColor)
            }


            if (remoteConfig.stepIndicatorComponent?.defaultStepCircleEndColor?.isValidColor() == true) {
                themeConfig.stepIndicatorComponent?.defaultStepCircleEndColor =
                    remoteConfig.stepIndicatorComponent?.defaultStepCircleEndColor
                        ?: context.resources.getString(R.color.defaultStepCircleEndColor)
            } else {
                themeConfig.stepIndicatorComponent?.defaultStepCircleEndColor =
                    context.resources.getString(R.color.defaultStepCircleEndColor)
            }
            if (remoteConfig.stepIndicatorComponent?.defaultStepCircleStartBorderColor?.isValidColor() == true) {
                themeConfig.stepIndicatorComponent?.defaultStepCircleStartBorderColor =
                    remoteConfig.stepIndicatorComponent?.defaultStepCircleStartBorderColor
                        ?: context.resources.getString(R.color.defaultStepCircleStartBorderColor)
            } else {
                themeConfig.stepIndicatorComponent?.defaultStepCircleStartBorderColor =
                    context.resources.getString(R.color.defaultStepCircleStartBorderColor)
            }

            if (remoteConfig.stepIndicatorComponent?.defaultStepCircleEndBorderColor?.isValidColor() == true) {
                themeConfig.stepIndicatorComponent?.defaultStepCircleEndBorderColor =
                    remoteConfig.stepIndicatorComponent?.defaultStepCircleEndBorderColor
                        ?: context.resources.getString(R.color.defaultStepCircleEndBorderColor)
            } else {
                themeConfig.stepIndicatorComponent?.defaultStepCircleEndBorderColor =
                    context.resources.getString(R.color.defaultStepCircleEndBorderColor)
            }


            if (remoteConfig.stepIndicatorComponent?.selectedStepCircleStartColor?.isValidColor() == true) {
                themeConfig.stepIndicatorComponent?.selectedStepCircleStartColor =
                    remoteConfig.stepIndicatorComponent?.selectedStepCircleStartColor
                        ?: context.resources.getString(R.color.selectedStepCircleStartColor)
            } else {
                themeConfig.stepIndicatorComponent?.selectedStepCircleStartColor =
                    context.resources.getString(R.color.selectedStepCircleStartColor)
            }
            if (remoteConfig.stepIndicatorComponent?.selectedStepCircleEndColor?.isValidColor() == true) {
                themeConfig.stepIndicatorComponent?.selectedStepCircleEndColor =
                    remoteConfig.stepIndicatorComponent?.selectedStepCircleEndColor
                        ?: context.resources.getString(R.color.selectedIndicatorEndColor)
            } else {
                themeConfig.stepIndicatorComponent?.selectedStepCircleEndColor =
                    context.resources.getString(R.color.selectedIndicatorEndColor)
            }

            if (remoteConfig.stepIndicatorComponent?.selectedStepCircleStartBorderColor?.isValidColor() == true) {
                themeConfig.stepIndicatorComponent?.selectedStepCircleStartBorderColor =
                    remoteConfig.stepIndicatorComponent?.selectedStepCircleStartBorderColor
                        ?: context.resources.getString(R.color.selectedStepCircleStartBorderColor)
            } else {
                themeConfig.stepIndicatorComponent?.selectedStepCircleStartBorderColor =
                    context.resources.getString(R.color.selectedStepCircleStartBorderColor)
            }
            if (remoteConfig.stepIndicatorComponent?.selectedStepCircleEndBorderColor?.isValidColor() == true) {
                themeConfig.stepIndicatorComponent?.selectedStepCircleEndBorderColor =
                    remoteConfig.stepIndicatorComponent?.selectedStepCircleEndBorderColor
                        ?: context.resources.getString(R.color.selectedStepCircleEndBorderColor)
            } else {
                themeConfig.stepIndicatorComponent?.selectedStepCircleEndBorderColor =
                    context.resources.getString(R.color.selectedStepCircleEndBorderColor)
            }
        }
        this.validThemeConfig = themeConfig
        return this
    }

    fun buildWithStat(): ThemeConfigBuilder {
        val themeConfig = ThemeConfig()
        themeConfig.mainComponent = themeConfig.MainComponent().apply {
            this.mainContainerColorConfig = ContainerConfig(
                startBorderColor = "#C3B2FF",
                endBorderColor = "#301EAE",
                startColor = "#A38AFE",
                endColor = "#4734D5"
            )
            this.secondaryContainerColorConfig = ContainerConfig(
                startBorderColor = "#B1A6FF",
                endBorderColor = "#2F209E",
                startColor = "#806CED",
                endColor = "#4835D5"
            )
            this.textColorPrimary = "#FFB959"
            this.textColorSecondary = "#DCDFE4"
            this.textColorFocus = "#FFFFFF"
            this.textColorFocusSecondary = "#FFB959"
        }
        themeConfig.buttonComponent = themeConfig.ButtonComponent().apply {
            this.primary = ContainerConfig(
                startColor = "#FFB959",
                endColor = "#EC9C2E",
                startBorderColor = "#FFDBA9",
                endBorderColor = "#996F35",
            )
            this.secodary = ContainerConfig(
                startColor = "#886EEC",
                endColor = "#6250D6",
                startBorderColor = "#B4A1FF",
                endBorderColor = "#20117F",
            )
        }

        this.validThemeConfig = themeConfig
        return this
    }

    fun getTheme(): ThemeConfig? {
        return if (::validThemeConfig.isInitialized)
            validThemeConfig
        else
            null
    }
}