package br.com.alura.technews.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.addNews.FormularioNoticiaActivity
import br.com.alura.technews.ui.extensions.fragment.transacaoFragment
import br.com.alura.technews.ui.listOfNews.ListOfNewsFragment
import br.com.alura.technews.ui.seeNews.VisualizaNoticiaFragment

/**
 * This activity represents the manager of news layout fragments.
 */
internal class NewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        if (savedInstanceState == null) abreListaNoticias()
    }

    private fun abreListaNoticias() {
        transacaoFragment { replace(R.id.activity_news_container, ListOfNewsFragment()) }
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
        val transacao = supportFragmentManager.beginTransaction()
        val fragment = VisualizaNoticiaFragment()
        val dados = Bundle()

        dados.putLong(NOTICIA_ID_CHAVE, noticia.id)
        fragment.arguments = dados

        transacaoFragment {
            transacao.replace(R.id.activity_news_container, fragment)
            transacao.commit()
        }
    }

    private fun abreFormularioEdicao(noticia: Noticia) {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        intent.putExtra(NOTICIA_ID_CHAVE, noticia.id)
        startActivity(intent)
    }
}