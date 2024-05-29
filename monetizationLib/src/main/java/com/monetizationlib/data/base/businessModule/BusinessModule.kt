package com.monetizationlib.data.base.businessModule

import com.monetizationlib.data.base.model.Model

/**
 * This layer stands between the View Model and the Model.
 * Encapsulates all business logic and gives a great API to the View Model
 * to talk with the Model.
 *
 * Completely testable and loosely coupled
 */
abstract class BusinessModule<M> where M : Model {
    abstract val model: M
}