package br.com.alura.technews.ui.extensions.fragment

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

fun Fragment.mostraMensagem(mensagem: String) {
    Toast.makeText(
        context,
        mensagem,
        Toast.LENGTH_SHORT
    ).show()
}

fun AppCompatActivity.transacaoFragment(executa: FragmentTransaction.() -> Unit) {
    val transacao = supportFragmentManager.beginTransaction()
    executa(transacao)
    transacao.commit()
}