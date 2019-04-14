package ru.appkode.base.entities.core.util

fun <T> T?.requireField(name: String) = this ?: throw DataMappingException("$name is required")

class DataMappingException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
