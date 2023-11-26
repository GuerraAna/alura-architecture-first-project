package br.com.alura.technews.ui.listOfNews

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.Resource

internal class NewsListViewModel(
    private val repository: NoticiaRepository
) : ViewModel() {

    fun buscaTodos(): LiveData<Resource<List<Noticia>?>> = repository.buscaTodos()
}