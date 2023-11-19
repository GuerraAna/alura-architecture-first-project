package br.com.alura.technews.repository

internal open class Resource<T>(
    val dado: T?,
    val erro: String? = null
)

internal class SucessoResource<T>(dado: T?): Resource<T>(dado)

internal class FalhaResource<T>(erro: String?): Resource<T>(dado = null, erro = erro)