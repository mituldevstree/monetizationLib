package com.monetizationlib.data.ads

object LoaderTypeHelper {
    var latestLoaderUsedType: LoaderType? = null
}

enum class LoaderType {
    APPLOVIN,
    FAIRBID,
    IRONSOURCE
}