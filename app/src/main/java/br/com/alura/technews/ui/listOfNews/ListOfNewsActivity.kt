package br.com.alura.technews.ui.listOfNews

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.NOTICIA_ID_CHAVE
import br.com.alura.technews.ui.addNews.FormularioNoticiaActivity
import br.com.alura.technews.ui.fragment.ListOfNewsFragment
import br.com.alura.technews.ui.seeNews.VisualizaNoticiaActivity

/**
 * This activity represents the list of news from local API.
 */
internal class ListOfNewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_news)
        title = getString(R.string.last_news)
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        if (fragment is ListOfNewsFragment) {
            fragment.quandoNoticiasSelectiona = { noticia -> abreVisualizadorNoticia(noticia) }
            fragment.quandoFabSalvaNoticiasClicado = { abreFormularioModoCriacao() }
        }
    }

    private fun abreFormularioModoCriacao() {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        startActivity(intent)
    }

    private fun abreVisualizadorNoticia(noticia: Noticia) {
        val intent = Intent(this, VisualizaNoticiaActivity::class.java)
        intent.putExtra(NOTICIA_ID_CHAVE, noticia.id)
        startActivity(intent)
    }
}