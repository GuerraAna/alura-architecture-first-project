package br.com.alura.technews.ui.addNews

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.Resource

internal class FormularioNoticiaViewModel(
    private val repository: NoticiaRepository
) : ViewModel() {

    /**
     * Search for the details of selected or not news item Id.
     */
    fun buscaPorId(noticiaId: Long): LiveData<Resource<Noticia?>> =
        repository.buscaPorId(noticiaId = noticiaId)

    /**
     * If news id is upper than zero, should edit changes of news.
     * Otherwise, should create another new.
     */
    fun editaOuSalva(noticia: Noticia): LiveData<Resource<Noticia?>> =
        when (noticia.id > 0) {
            true -> repository.edita(noticia)
            else -> repository.salva(noticia)
        }
}