package br.com.alura.technews.ui.seeNews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.Resource

internal class VisualizaNoticiaViewModel(
    id: Long,
    private val repository: NoticiaRepository
) : ViewModel() {

    val searchedNews = repository.buscaPorId(id)

    /**
     * Remove the selected news from API.
     */
    fun remove(): LiveData<Resource<Noticia?>> {
        return searchedNews.value?.run {
            repository.remove(this)
        } ?: MutableLiveData<Resource<Noticia?>>().also {
            it.value = Resource(dado = null, erro = "Noticia não encontrada")
        }
    }
}