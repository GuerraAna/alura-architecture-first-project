package br.com.alura.technews.ui.seeNews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.FalhaResource
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.Resource

internal class VisualizaNoticiaViewModel(
    private val id: Long,
    private val repository: NoticiaRepository
): ViewModel() {

    private val searchedNews = buscaPorId()

    /**
     * Search for the details of selected or not news item Id.
     */
    fun buscaPorId(): LiveData<Resource<Noticia?>> = repository.buscaPorId(id)

    /**
     *
     */
    fun remove(): LiveData<Resource<Noticia?>> {
        return searchedNews.value?.run {
            repository.remove(this.dado!!)
        } ?: MutableLiveData<Resource<Noticia?>>().also {
            it.value = FalhaResource(erro = "Noticia não encontrada")
        }
    }
}