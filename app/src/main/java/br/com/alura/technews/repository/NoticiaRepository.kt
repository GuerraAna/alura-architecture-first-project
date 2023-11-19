package br.com.alura.technews.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.alura.technews.asynctask.BaseAsyncTask
import br.com.alura.technews.database.dao.NoticiaDAO
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.retrofit.webclient.NoticiaWebClient

internal class NoticiaRepository(
    private val dao: NoticiaDAO,
    private val webclient: NoticiaWebClient = NoticiaWebClient()
) {

    private val noticiasEncontradas = MutableLiveData<Resource<List<Noticia>?>>()

    fun buscaTodos(): LiveData<Resource<List<Noticia>?>> {
        val atualizaListaNoticias: (List<Noticia>) -> Unit = {
            noticiasEncontradas.value = SucessoResource(dado = it)
        }

        buscaInterno(quandoSucesso = atualizaListaNoticias)
        buscaNaApi(
            quandoSucesso = atualizaListaNoticias,
            quandoFalha = { erro ->
                noticiasEncontradas.value = FalhaResource<List<Noticia>?>(erro)
            }
        )

        return noticiasEncontradas
    }

    fun salva(noticia: Noticia): LiveData<Resource<Noticia?>> {
        val liveData = MutableLiveData<Resource<Noticia?>>()

        salvaNaApi(
            noticia = noticia,
            quandoSucesso = { liveData.value = SucessoResource(null)},
            quandoFalha = { liveData.value = FalhaResource(erro = it) }
        )

        return liveData
    }

    fun remove(noticia: Noticia): LiveData<Resource<Noticia?>> {
        val liveData = MutableLiveData<Resource<Noticia?>>()

        removeNaApi(
            noticia = noticia,
            quandoSucesso = { liveData.value = SucessoResource(dado = null) },
            quandoFalha = { liveData.value = FalhaResource(erro = it) }
        )

        return liveData
    }

    fun edita(noticia: Noticia): LiveData<Resource<Noticia?>> {
        val liveData = MutableLiveData<Resource<Noticia?>>()

        editaNaApi(
            noticia = noticia,
            quandoSucesso = { liveData.value = SucessoResource(null) },
            quandoFalha = { liveData.value = FalhaResource(it) }
        )

        return liveData
    }

    fun buscaPorId(noticiaId: Long): LiveData<Resource<Noticia?>> {
        val liveData = MutableLiveData<Resource<Noticia?>>()

        BaseAsyncTask(
            quandoExecuta = { dao.buscaPorId(noticiaId) },
            quandoFinaliza = {
                if (it != null) {
                    liveData.value = SucessoResource(it)
                } else {
                    liveData.value = FalhaResource("vazioooooo")
                }
            }
        ).execute()

        return liveData
    }

    private fun buscaNaApi(
        quandoSucesso: (List<Noticia>) -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.buscaTodas(
            quandoSucesso = { noticiasNovas ->
                noticiasNovas?.let { salvaInterno(noticiasNovas, quandoSucesso) }
            },
            quandoFalha = quandoFalha
        )
    }

    private fun buscaInterno(quandoSucesso: (List<Noticia>) -> Unit) {
        BaseAsyncTask(
            quandoExecuta = { dao.buscaTodos() },
            quandoFinaliza = quandoSucesso
        ).execute()
    }


    private fun salvaNaApi(
        noticia: Noticia,
        quandoSucesso: (noticiaNova: Noticia) -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.salva(
            noticia,
            quandoSucesso = {
                it?.let { noticiaSalva ->
                    salvaInterno(noticiaSalva, quandoSucesso)
                }
            }, quandoFalha = quandoFalha
        )
    }

    private fun salvaInterno(
        noticias: List<Noticia>,
        quandoSucesso: (noticiasNovas: List<Noticia>) -> Unit
    ) {
        BaseAsyncTask(
            quandoExecuta = {
                dao.salva(noticias)
                dao.buscaTodos()
            },
            quandoFinaliza = quandoSucesso
        ).execute()
    }

    private fun salvaInterno(
        noticia: Noticia,
        quandoSucesso: (noticiaNova: Noticia) -> Unit
    ) {
        BaseAsyncTask(quandoExecuta = {
            dao.salva(noticia)
            dao.buscaPorId(noticia.id)
        }, quandoFinaliza = { noticiaEncontrada ->
            noticiaEncontrada?.let {
                quandoSucesso(it)
            }
        }).execute()

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
        BaseAsyncTask(quandoExecuta = {
            dao.remove(noticia)
        }, quandoFinaliza = {
            quandoSucesso()
        }).execute()
    }

    private fun editaNaApi(
        noticia: Noticia,
        quandoSucesso: (noticiaEditada: Noticia) -> Unit,
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
