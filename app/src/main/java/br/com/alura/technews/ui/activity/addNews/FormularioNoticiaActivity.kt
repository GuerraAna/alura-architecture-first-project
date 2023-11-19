package br.com.alura.technews.ui.activity.addNews

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
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.ui.activity.NOTICIA_ID_CHAVE
import br.com.alura.technews.ui.activity.extensions.mostraErro
import kotlinx.android.synthetic.main.activity_formulario_noticia.activity_formulario_noticia_texto
import kotlinx.android.synthetic.main.activity_formulario_noticia.activity_formulario_noticia_titulo

private const val TITULO_APPBAR_EDICAO = "Editando notícia"
private const val TITULO_APPBAR_CRIACAO = "Criando notícia"
private const val MENSAGEM_ERRO_SALVAR = "Não foi possível salvar notícia"

internal class FormularioNoticiaActivity : AppCompatActivity() {

    private val noticiaId: Long by lazy {
        intent.getLongExtra(NOTICIA_ID_CHAVE, 0)
    }

    private val viewModel by lazy {
        val repository = NoticiaRepository(AppDatabase.getInstance(this@FormularioNoticiaActivity).noticiaDAO)
        val factory = FormularioNoticiasViewModelFactory(repository)
        val provider = ViewModelProviders.of(this, factory)

        provider.get(FormularioNoticiaViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_noticia)

        definindoTitulo()
        preencheFormulario()
    }

    private fun definindoTitulo() {
        title = if (noticiaId > 0) {
            TITULO_APPBAR_EDICAO
        } else {
            TITULO_APPBAR_CRIACAO
        }
    }

    private fun preencheFormulario() {
        viewModel.buscaPorId(noticiaId).observe(
            this,
            Observer {
                if (it != null) {
                    activity_formulario_noticia_titulo.setText(it.titulo)
                    activity_formulario_noticia_texto.setText(it.texto)
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
