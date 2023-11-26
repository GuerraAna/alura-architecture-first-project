package br.com.alura.technews.ui.listOfNews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.extensions.fragment.mostraMensagem
import kotlinx.android.synthetic.main.news_list_fragment.lista_noticias_fab_salva_noticia
import kotlinx.android.synthetic.main.news_list_fragment.lista_noticias_recyclerview
import org.koin.android.viewmodel.ext.android.viewModel

private const val FAILED_TO_LOAD_NEWS_MESSAGE = "Falha ao carregar a lista de notícias! :("

internal class ListOfNewsFragment : Fragment() {

    private val adapter by lazy {
        context?.let {
            ListaNoticiasAdapter(context = it)
        } ?: throw IllegalArgumentException("Contexto inválido para a classe ListOFNewsFragment")
    }

    private val viewModel: NewsListViewModel by viewModel()
    var quandoFabSalvaNoticiasClicado: () -> Unit = {}
    var quandoNoticiasSelectiona: (noticia: Noticia) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buscaNoticias()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.news_list_fragment,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configuraRecyclerView()
        configuraFabAdicionaNoticias()
        activity?.title = "Notícias"
    }

    private fun configuraRecyclerView() {
        val divisor = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        lista_noticias_recyclerview.addItemDecoration(divisor)
        lista_noticias_recyclerview.adapter = adapter
        configuraAdapter()
    }

    private fun configuraFabAdicionaNoticias() {
        lista_noticias_fab_salva_noticia.setOnClickListener { quandoFabSalvaNoticiasClicado() }
    }

    private fun configuraAdapter() {
        adapter.quandoItemClicado = quandoNoticiasSelectiona
    }

    private fun buscaNoticias() {
        viewModel.buscaTodos().observe(
            this,
            Observer { resource ->
                resource.dado?.let { adapter.atualiza(it) }
                resource.erro?.let { mostraMensagem(FAILED_TO_LOAD_NEWS_MESSAGE) }
            }
        )
    }
}