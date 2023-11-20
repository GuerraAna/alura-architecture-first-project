package br.com.alura.technews.repository

internal open class Resource<T>(
    val dado: T?,
    val erro: String? = null
)