package br.com.alura.technews.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.addNews.FormularioNoticiaActivity
import br.com.alura.technews.ui.extensions.fragment.transacaoFragment
import br.com.alura.technews.ui.listOfNews.ListOfNewsFragment
import br.com.alura.technews.ui.seeNews.VisualizaNoticiaFragment
import kotlinx.android.synthetic.main.activity_news.activity_noticias_container_secundario

private const val TAG_FRAGMENT_VISUALIZA_NOTICIA = "visualizaNoticia"

/**
 * This activity represents the manager of news layout fragments.
 */
internal class NewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        when (savedInstanceState == null) {
            true -> abreListaNoticias()
            else -> tentaReabrirFragmentVisualizaNoticia()
        }
    }

    private fun tentaReabrirFragmentVisualizaNoticia() {
        supportFragmentManager
            .findFragmentByTag(TAG_FRAGMENT_VISUALIZA_NOTICIA)?.let { fragment ->
                val argumentos = fragment.arguments
                val novoFragment = VisualizaNoticiaFragment()

                novoFragment.arguments = argumentos
                removeFragmentVisualizaNoticia(fragment)

                transacaoFragment {
                    replace(
                        /* containerViewId = */ configuraContainervisualizaNoticia(),
                        /* fragment = */ novoFragment,
                        /* tag = */ TAG_FRAGMENT_VISUALIZA_NOTICIA
                    )
                }
            }
    }

    private fun FragmentTransaction.configuraContainervisualizaNoticia() =
        when (activity_noticias_container_secundario != null) {
            true -> R.id.activity_noticias_container_secundario
            else -> {
                addToBackStack(null)
                R.id.activity_noticias_container_primario
            }
        }

    private fun abreListaNoticias() {
        transacaoFragment {
            replace(R.id.activity_noticias_container_primario, ListOfNewsFragment())
        }
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
        fragment.quandoFinalizaTela = {
            supportFragmentManager
                .findFragmentByTag(TAG_FRAGMENT_VISUALIZA_NOTICIA)?.let { fragment ->
                    transacaoFragment { removeFragmentVisualizaNoticia(fragment) }
                }
        }

        fragment.quandoSelecionaMenuEdicao = this::abreFormularioEdicao
    }

    private fun removeFragmentVisualizaNoticia(fragment: Fragment) {
        transacaoFragment { remove(fragment) }
        supportFragmentManager.popBackStack()
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
                when (activity_noticias_container_secundario != null) {
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