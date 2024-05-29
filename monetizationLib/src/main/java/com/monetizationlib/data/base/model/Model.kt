package com.monetizationlib.data.base.model

import com.monetizationlib.data.base.model.cloud.cache.Cache
import com.monetizationlib.data.base.model.cloud.layerSpecifics.Cacheable
import com.monetizationlib.data.base.model.localLayer.layerSpecifics.Storage
import com.monetizationlib.data.base.model.localLayer.layerSpecifics.StorageInternal
import com.monetizationlib.data.base.model.networkLayer.layerSpecifics.NetworkFacade

/**
 * The default Model Layer. The Model layer is represented in
 * every Module and contains Three Inner Layers -> Local Storage, Network Facade, Cache.
 *
 * This class represents that and has a purpose of being a default one.
 */
abstract class Model : ModelInternal<Storage, NetworkFacade<*>, Cache>() {
    override val localStorage = Storage()

    override val cache = Cache()
}

/**
 * Sealed hidden class which's purpose is to be inherited by the
 * Default Model Layer.
 */
sealed class ModelInternal<S, N, C> where S : StorageInternal,
                                          N : NetworkFacade<*>,
                                          C : Cacheable {
    abstract val localStorage: S

    abstract val networkFacade: N

    abstract val cache: C
}