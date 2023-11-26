package br.com.alura.technews.ui.seeNews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.NOTICIA_ID_CHAVE
import br.com.alura.technews.ui.extensions.fragment.mostraMensagem
import kotlinx.android.synthetic.main.visualiza_noticia_fragment.activity_visualiza_noticia_texto
import kotlinx.android.synthetic.main.visualiza_noticia_fragment.activity_visualiza_noticia_titulo
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

internal class VisualizaNoticiaFragment : Fragment() {

    private val noticiaId: Long by lazy {
        arguments?.getLong(NOTICIA_ID_CHAVE)
            ?: throw IllegalArgumentException("O ID da notícia é inválido")
    }

    private val viewModel: VisualizaNoticiaViewModel by viewModel { parametersOf(noticiaId) }
    var quandoSelecionaMenuEdicao: (noticia: Noticia) -> Unit = {}
    var quandoFinalizaTela: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        buscaNoticiaSelecionada()
        verificaIdDaNoticia()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.visualiza_noticia_fragment,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Noticia"
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.visualiza_noticia_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.visualiza_noticia_menu_edita -> {
                viewModel.searchedNews.value?.let { quandoSelecionaMenuEdicao(it) }
            }

            R.id.visualiza_noticia_menu_remove -> remove()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun buscaNoticiaSelecionada() {
        viewModel.searchedNews.observe(
            this,
            Observer { noticiaEncontrada -> noticiaEncontrada?.let { preencheCampos(it) } }
        )
    }

    private fun verificaIdDaNoticia() {
        if (noticiaId == 0L) {
            mostraMensagem(getString(R.string.news_not_found))
            quandoFinalizaTela()
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
                    else -> mostraMensagem(getString(R.string.cant_delete_news))
                }
            }
        )
    }

    private fun onRemovedNews() {
        mostraMensagem("Removido com sucesso")
        quandoFinalizaTela()
    }
}