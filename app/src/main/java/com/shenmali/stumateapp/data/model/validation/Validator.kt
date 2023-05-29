package com.shenmali.stumateapp.data.model.validation

interface Validator<T> {
    fun validate(args: T)
}