package br.com.alura.technews.ui.addNews

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.NOTICIA_ID_CHAVE
import br.com.alura.technews.ui.extensions.mostraErro
import kotlinx.android.synthetic.main.activity_formulario_noticia.activity_formulario_noticia_texto
import kotlinx.android.synthetic.main.activity_formulario_noticia.activity_formulario_noticia_titulo
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * This activity represents the edition mode of news.
 * The user can save or edit the news details.
 */
internal class FormularioNoticiaActivity : AppCompatActivity() {

    private val noticiaId: Long by lazy { intent.getLongExtra(NOTICIA_ID_CHAVE, 0) }
    private val viewModel by viewModel<FormularioNoticiaViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_noticia)

        definindoTitulo()
        preencheFormulario()
    }

    private fun definindoTitulo() {
        title = when (noticiaId > 0) {
            true -> getString(R.string.edit_news)
            else -> getString(R.string.create_news)
        }
    }

    private fun preencheFormulario() {
        viewModel.buscaPorId(noticiaId).observe(
            this,
            Observer { noticiaEncontrada ->
                if (noticiaEncontrada != null) {
                    activity_formulario_noticia_titulo.setText(noticiaEncontrada.titulo)
                    activity_formulario_noticia_texto.setText(noticiaEncontrada.texto)
                }
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.formulario_noticia_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.formulario_noticia_salva -> {
                val titulo = activity_formulario_noticia_titulo.text.toString()
                val texto = activity_formulario_noticia_texto.text.toString()

                salva(Noticia(noticiaId, titulo, texto))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun salva(noticia: Noticia) {
        viewModel.editaOuSalva(noticia).observe(
            this,
            Observer {
                when (it.erro == null) {
                    true -> {
                        Toast.makeText(this, "Sucesso ao salvar", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                    else -> mostraErro("Erro ao salvar")
                }
            }
        )
    }
}
