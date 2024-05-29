package com.monetizationlib.data.network


import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import java.net.HttpURLConnection.HTTP_CREATED
import java.net.HttpURLConnection.HTTP_OK

object ApiEndpoints {



    //headers
    const val HEADER_LANGUAGE = "language"
    const val HEADER_PACKAGE_NAME = "packageName"
    const val HEADER_VERSION = "version"
    const val HEADER_CURRENCY = "currency"
    const val HEADER_SESSION = "authcode"

    // response code
    const val RESPONSE_OK = HTTP_OK
    const val RESPONSE_CREATED = HTTP_CREATED
    const val RESPONSE_ERROR = HTTP_BAD_REQUEST

    /**
     * User Enrollment apis
     */
    const val VERSION_CHECK = "checkVersionEstablished"
    const val REGISTER_DEVICE = "loginEstablished"


}