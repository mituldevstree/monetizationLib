package com.monetizationlib.data.base.model.localLayer.layerSpecifics

import com.monetizationlib.data.base.model.localLayer.persistance.Preferences

/**
 * Default class representing the Local Storage Layer.
 */
open class Storage : StorageInternal() {
    override val preferences = Preferences()
}

/**
 * Class which is extended only from the same namespace.
 * The base for the "Local Storage Layer"
 */
sealed class StorageInternal {
    abstract val preferences: Preferences
}