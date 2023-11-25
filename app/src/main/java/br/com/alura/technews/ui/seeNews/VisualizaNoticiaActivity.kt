
package br.com.alura.technews.ui.seeNews

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.alura.technews.R
import br.com.alura.technews.ui.NOTICIA_ID_CHAVE
import br.com.alura.technews.ui.addNews.FormularioNoticiaActivity
import br.com.alura.technews.ui.fragment.VisualizaNoticiaFragment

/**
 * This activity represents the details of selected news.
 */
internal class VisualizaNoticiaActivity : AppCompatActivity() {

    private val noticiaId: Long by lazy { intent.getLongExtra(NOTICIA_ID_CHAVE, 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualiza_noticia)
        title = getString(R.string.news)

        setupFragmentDetails()
    }

    private fun setupFragmentDetails() {
        val transacao = supportFragmentManager.beginTransaction()
        val fragment = VisualizaNoticiaFragment()
        val dados = Bundle()
        dados.putLong(NOTICIA_ID_CHAVE, noticiaId)
        fragment.arguments = dados
        transacao.add(R.id.visualiza_noticia_fragment_container, fragment)
        transacao.commit()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        if (fragment is VisualizaNoticiaFragment) {
            fragment.quandoFinalizaTela = { finish() }
            fragment.quandoSelecionaMenuEdicao = { abreFormularioEdicao() }
        }
    }

    private fun abreFormularioEdicao() {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        intent.putExtra(NOTICIA_ID_CHAVE, noticiaId)
        startActivity(intent)
    }
}