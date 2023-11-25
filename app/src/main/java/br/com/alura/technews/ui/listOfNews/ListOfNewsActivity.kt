package br.com.alura.technews.ui.listOfNews

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.NOTICIA_ID_CHAVE
import br.com.alura.technews.ui.addNews.FormularioNoticiaActivity
import br.com.alura.technews.ui.extensions.mostraErro
import br.com.alura.technews.ui.seeNews.VisualizaNoticiaActivity
import kotlinx.android.synthetic.main.activity_list_of_news.activity_lista_noticias_fab_salva_noticia
import kotlinx.android.synthetic.main.activity_list_of_news.activity_lista_noticias_recyclerview
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * This activity represents the list of news from local API.
 */
internal class ListOfNewsActivity : AppCompatActivity() {

    private val adapter by lazy { ListaNoticiasAdapter(context = this) }
    private val viewModel: NewsListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_news)

        // Setup title of toolbar
        title = getString(R.string.last_news)

        setupListOfNews()
        setupListeners()
        searchNews()
    }

    private fun setupListeners() {
        activity_lista_noticias_fab_salva_noticia.setOnClickListener { abreFormularioModoCriacao() }
    }

    private fun setupListOfNews() {
        val divisor = DividerItemDecoration(this, VERTICAL)

        activity_lista_noticias_recyclerview.addItemDecoration(divisor)
        activity_lista_noticias_recyclerview.adapter = adapter
        configuraAdapter()
    }

    private fun configuraAdapter() {
        adapter.quandoItemClicado = this::abreVisualizadorNoticia
    }

    private fun searchNews() {
        viewModel.buscaTodos().observe(
            this,
            Observer { resource ->
                resource.dado?.let { adapter.atualiza(it) }
                resource.erro?.let { mostraErro(getString(R.string.load_list_of_news_message_of_error)) }
            }
        )
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