package br.com.alura.technews.ui.extensions.activity

import android.app.Activity
import android.widget.Toast

fun Activity.mostraErro(mensagem: String) {
    Toast.makeText(
        this,
        mensagem,
        Toast.LENGTH_LONG
    ).show()
}