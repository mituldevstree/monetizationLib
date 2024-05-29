package com.monetizationlib.data.base.model.networkLayer.layerSpecifics

import com.monetizationlib.data.base.model.networkLayer.networking.CommonService
import com.monetizationlib.data.base.model.networkLayer.networking.NetworkManager

/**
 * The default network facade is a class
 * which gives a default API to all other classes
 * for making network requests by using the endpoints in CommonService
 */
open class BaseNetworkFacade : NetworkFacade<CommonService>() {
    override val apiService: CommonService = NetworkManager.defaultApiService
}
