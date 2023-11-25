package br.com.alura.technews.ui.fragment.extensions

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.mostraMensagem(mensagem: String) {
    Toast.makeText(
        context,
        mensagem,
        Toast.LENGTH_SHORT
    ).show()
}