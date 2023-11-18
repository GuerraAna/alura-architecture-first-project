package br.com.alura.technews.ui.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.Resource

internal class NewsListViewModel(
    private val repository: NoticiaRepository
) : ViewModel() {

    init {
        Log.i("ViewModel", "criando viewModel!")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("ViewModel", "destruindo viewModel!")
    }

    fun buscaTodos(): LiveData<Resource<List<Noticia>?>> = repository.buscaTodos()
}