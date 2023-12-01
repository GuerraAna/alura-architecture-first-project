package br.com.alura.technews.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.addNews.FormularioNoticiaActivity
import br.com.alura.technews.ui.extensions.fragment.transacaoFragment
import br.com.alura.technews.ui.listOfNews.ListOfNewsFragment
import br.com.alura.technews.ui.seeNews.VisualizaNoticiaFragment

private const val TAG_FRAGMENT_VISUALIZA_NOTICIA = "visualizaNoticia"

/**
 * This activity represents the manager of news layout fragments.
 */
internal class NewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        if (savedInstanceState == null) abreListaNoticias()

        when (savedInstanceState == null) {
            true -> abreListaNoticias()
            else -> {
                supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_VISUALIZA_NOTICIA)?.let { fragment ->
                    val argumentos = fragment.arguments
                    val novoFragment = VisualizaNoticiaFragment()
                    novoFragment.arguments = argumentos

                    transacaoFragment { remove(fragment) }
                    supportFragmentManager.popBackStack()

                    transacaoFragment {
                        val container =
                            when (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                true -> R.id.activity_noticias_container_secundario
                                else -> {
                                    addToBackStack(null)
                                    R.id.activity_noticias_container_primario
                                }
                            }

                        replace(container, novoFragment, TAG_FRAGMENT_VISUALIZA_NOTICIA)
                    }
                }
            }
        }
    }

    private fun abreListaNoticias() {
        transacaoFragment { replace(R.id.activity_noticias_container_primario, ListOfNewsFragment()) }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        when (fragment) {
            is ListOfNewsFragment -> configuraListOfNewsFragment(fragment)
            is VisualizaNoticiaFragment -> configuraVisualizaNoticiaFragment(fragment)
        }
    }

    private fun configuraListOfNewsFragment(fragment: ListOfNewsFragment) {
        fragment.quandoNoticiasSelectiona = this::abreVisualizadorNoticia
        fragment.quandoFabSalvaNoticiasClicado = this::abreFormularioModoCriacao
    }

    private fun configuraVisualizaNoticiaFragment(fragment: VisualizaNoticiaFragment) {
        fragment.quandoFinalizaTela = this::finish
        fragment.quandoSelecionaMenuEdicao = this::abreFormularioEdicao
    }

    private fun abreFormularioModoCriacao() {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        startActivity(intent)
    }

    private fun abreVisualizadorNoticia(noticia: Noticia) {
        val fragment = VisualizaNoticiaFragment()
        val dados = Bundle()

        dados.putLong(NOTICIA_ID_CHAVE, noticia.id)
        fragment.arguments = dados

        transacaoFragment {
            val container =
                when (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    true -> R.id.activity_noticias_container_secundario
                    else -> {
                        addToBackStack(null)
                        R.id.activity_noticias_container_primario
                    }
                }

            replace(container, fragment, TAG_FRAGMENT_VISUALIZA_NOTICIA)
        }
    }

    private fun abreFormularioEdicao(noticia: Noticia) {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        intent.putExtra(NOTICIA_ID_CHAVE, noticia.id)
        startActivity(intent)
    }
}