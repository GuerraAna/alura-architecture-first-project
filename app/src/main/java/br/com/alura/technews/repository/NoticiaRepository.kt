package br.com.alura.technews.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import br.com.alura.technews.asynctask.BaseAsyncTask
import br.com.alura.technews.database.dao.NoticiaDAO
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.retrofit.webclient.NoticiaWebClient

internal class NoticiaRepository(
    private val dao: NoticiaDAO,
    private val webclient: NoticiaWebClient = NoticiaWebClient()
) {

    private val mediador = MediatorLiveData<Resource<List<Noticia>?>>()

    fun buscaTodos(): LiveData<Resource<List<Noticia>?>> {
        mediador.addSource(buscaInterno()) { noticiasEncontradas ->
            mediador.value = Resource(dado = noticiasEncontradas)
        }

        val falhasDaWebApiLiveData = MutableLiveData<Resource<List<Noticia>?>>()
        mediador.addSource(falhasDaWebApiLiveData) { resourceDeFalha ->
            val resourceAtual = mediador.value
            val resourceNovo: Resource<List<Noticia>?> =
                if (resourceAtual != null) {
                    Resource(
                        dado = resourceAtual.dado,
                        erro = resourceDeFalha.erro
                    )
                } else {
                    resourceDeFalha
                }

            mediador.value = resourceNovo
        }

        buscaNaApi(
            quandoFalha = { erro ->
                falhasDaWebApiLiveData.value =
                    Resource(dado = null, erro = erro)
            }
        )

        return mediador
    }

    fun salva(noticia: Noticia): LiveData<Resource<Noticia?>> {
        val liveData = MutableLiveData<Resource<Noticia?>>()

        salvaNaApi(
            noticia = noticia,
            quandoSucesso = { liveData.value = Resource(null) },
            quandoFalha = { liveData.value = Resource(dado = null, erro = it) }
        )

        return liveData
    }

    fun remove(noticia: Noticia): LiveData<Resource<Noticia?>> {
        val liveData = MutableLiveData<Resource<Noticia?>>()

        removeNaApi(
            noticia = noticia,
            quandoSucesso = { liveData.value = Resource(dado = null) },
            quandoFalha = { erro -> liveData.value = Resource(dado = null, erro = erro) }
        )

        return liveData
    }

    fun edita(noticia: Noticia): LiveData<Resource<Noticia?>> {
        val liveData = MutableLiveData<Resource<Noticia?>>()

        editaNaApi(
            noticia = noticia,
            quandoSucesso = { liveData.value = Resource(null) },
            quandoFalha = { erro -> liveData.value = Resource(dado = null, erro = erro) }
        )

        return liveData
    }

    fun buscaPorId(noticiaId: Long): LiveData<Noticia?> = dao.buscaPorId(noticiaId)

    private fun buscaNaApi(quandoFalha: (erro: String?) -> Unit) {
        webclient.buscaTodas(
            quandoSucesso = { noticiasNovas ->
                noticiasNovas?.let { salvaInterno(noticiasNovas) }
            },
            quandoFalha = quandoFalha
        )
    }

    private fun buscaInterno(): LiveData<List<Noticia>> = dao.buscaTodos()

    private fun salvaNaApi(
        noticia: Noticia,
        quandoSucesso: () -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.salva(
            noticia,
            quandoSucesso = {
                it?.let { noticiaSalva ->
                    salvaInterno(
                        noticias = noticiaSalva,
                        quandoSucesso = quandoSucesso)
                }
            }, quandoFalha = quandoFalha
        )
    }

    private fun salvaInterno(noticias: List<Noticia>) {
        BaseAsyncTask(
            quandoExecuta = { dao.salva(noticias) },
            quandoFinaliza = { }
        ).execute()
    }

    private fun salvaInterno(
        noticias: Noticia,
        quandoSucesso: () -> Unit
    ) {
        BaseAsyncTask(
            quandoExecuta = { dao.salva(noticias) },
            quandoFinaliza = { quandoSucesso() }
        ).execute()
    }

    private fun removeNaApi(
        noticia: Noticia,
        quandoSucesso: () -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.remove(
            noticia.id,
            quandoSucesso = { removeInterno(noticia, quandoSucesso) },
            quandoFalha = quandoFalha
        )
    }


    private fun removeInterno(
        noticia: Noticia,
        quandoSucesso: () -> Unit
    ) {
        BaseAsyncTask(
            quandoExecuta = { dao.remove(noticia) },
            quandoFinaliza = { quandoSucesso() }
        ).execute()
    }

    private fun editaNaApi(
        noticia: Noticia,
        quandoSucesso: () -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.edita(
            id = noticia.id,
            noticia = noticia,
            quandoSucesso = { noticiaEditada ->
                noticiaEditada?.let { salvaInterno(noticiaEditada, quandoSucesso) }
            },
            quandoFalha = quandoFalha
        )
    }

}
