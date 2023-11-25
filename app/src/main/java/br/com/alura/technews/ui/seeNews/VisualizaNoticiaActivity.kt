
package br.com.alura.technews.ui.seeNews

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.NOTICIA_ID_CHAVE
import br.com.alura.technews.ui.addNews.FormularioNoticiaActivity
import br.com.alura.technews.ui.extensions.mostraErro
import kotlinx.android.synthetic.main.activity_visualiza_noticia.activity_visualiza_noticia_texto
import kotlinx.android.synthetic.main.activity_visualiza_noticia.activity_visualiza_noticia_titulo
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * This activity represents the details of selected news.
 */
class VisualizaNoticiaActivity : AppCompatActivity() {

    private val noticiaId: Long by lazy { intent.getLongExtra(NOTICIA_ID_CHAVE, 0) }
    private val viewModel: VisualizaNoticiaViewModel by viewModel { parametersOf(noticiaId) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualiza_noticia)

        title = getString(R.string.news)
        verificaIdDaNoticia()
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
        viewModel.searchedNews.observe(
            this,
            Observer { noticiaEncontrada ->
                noticiaEncontrada?.let { preencheCampos(it) }
            }
        )
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
        viewModel.remove().observe(
            this,
            Observer {
                when (it.erro == null) {
                    true -> onRemovedNews()
                    else -> mostraErro(getString(R.string.cant_delete_news))
                }
            }
        )
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
