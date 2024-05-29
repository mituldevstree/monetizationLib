package com.monetizationlib.data.base.model.networkLayer.layerSpecifics

/**
 * The base network facade is the parent class for
 * all network facades, in all different modules.
 */
abstract class NetworkFacade<API> {
    abstract val apiService: API
}