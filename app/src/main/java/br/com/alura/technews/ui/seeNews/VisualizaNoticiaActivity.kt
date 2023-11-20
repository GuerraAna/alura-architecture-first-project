
package br.com.alura.technews.ui.seeNews

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.alura.technews.R
import br.com.alura.technews.database.AppDatabase
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.FalhaResource
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.SucessoResource
import br.com.alura.technews.ui.NOTICIA_ID_CHAVE
import br.com.alura.technews.ui.addNews.FormularioNoticiaActivity
import br.com.alura.technews.ui.extensions.mostraErro
import kotlinx.android.synthetic.main.activity_visualiza_noticia.activity_visualiza_noticia_texto
import kotlinx.android.synthetic.main.activity_visualiza_noticia.activity_visualiza_noticia_titulo

/**
 * This activity represents the details of selected news.
 */
class VisualizaNoticiaActivity : AppCompatActivity() {

    private val noticiaId: Long by lazy { intent.getLongExtra(NOTICIA_ID_CHAVE, 0) }
    private val viewModel by lazy {
        val repository = NoticiaRepository(AppDatabase.getInstance(this@VisualizaNoticiaActivity).noticiaDAO)
        val factory = VisualizaNoticiaViewModelFactory(noticiaId, repository)
        val provider = ViewModelProviders.of(this@VisualizaNoticiaActivity, factory)

        provider.get(VisualizaNoticiaViewModel::class.java)
    }

    private lateinit var noticia: Noticia

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualiza_noticia)

        title = getString(R.string.news)
        verificaIdDaNoticia()
    }

    override fun onResume() {
        super.onResume()
        buscaNoticiaSelecionada()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.visualiza_noticia_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.visualiza_noticia_menu_edita -> abreFormularioEdicao()
            R.id.visualiza_noticia_menu_remove -> remove()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun buscaNoticiaSelecionada() {
        viewModel.buscaPorId().observe(
            this,
            Observer { resource ->
                when (resource) {
                    is SucessoResource -> onSelectedNews(resource.dado)
                    is FalhaResource -> mostraErro(resource.erro!!)
                }
            }
        )
    }

    private fun onSelectedNews(dado: Noticia?) {
        this.noticia = dado!!
        preencheCampos(dado)
    }

    private fun verificaIdDaNoticia() {
        if (noticiaId == 0L) {
            mostraErro(getString(R.string.news_not_found))
            finish()
        }
    }

    private fun preencheCampos(noticia: Noticia) {
        activity_visualiza_noticia_titulo.text = noticia.titulo
        activity_visualiza_noticia_texto.text = noticia.texto
    }

    private fun remove() {
        if (::noticia.isInitialized) {
            viewModel.remove().observe(
                this,
                Observer { noticia ->
                    when (noticia.erro == null) {
                        true -> onRemovedNews()
                        else -> mostraErro(getString(R.string.cant_delete_news))
                    }
                }
            )
        }
    }

    private fun onRemovedNews() {
        Toast.makeText(
            this,
            "Removido com sucesso",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }

    private fun abreFormularioEdicao() {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        intent.putExtra(NOTICIA_ID_CHAVE, noticiaId)
        startActivity(intent)
    }
}
