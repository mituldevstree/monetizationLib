package com.monetizationlib.data.base.model.cloud.cache

import com.monetizationlib.data.base.model.cloud.layerSpecifics.Cacheable

/**
 * The base class for all cache classes.
 * They all must be singletons since these classes
 * will hold the data which must be alive throughout the lifecycle of the app
 * but die with it when it dies.
 */
open class Cache : Cacheable