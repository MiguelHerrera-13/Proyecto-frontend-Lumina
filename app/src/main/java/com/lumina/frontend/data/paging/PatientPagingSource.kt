package com.lumina.frontend.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lumina.frontend.data.local.FakePatientDataSource
import com.lumina.frontend.data.local.SessionManager
import com.lumina.frontend.data.local.UserRole
import com.lumina.frontend.data.remote.PacienteApiService
import com.lumina.frontend.data.remote.dto.PacienteResponseDto

class PatientPagingSource(
    private val apiService: PacienteApiService
) : PagingSource<Int, PacienteResponseDto>() {

    companion object {
        const val STARTING_PAGE_INDEX = 0
        const val DEFAULT_PAGE_SIZE = 6
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PacienteResponseDto> {
        val page = params.key ?: STARTING_PAGE_INDEX
        val pageSize = params.loadSize

        return try {
            val response = apiService.obtenerPacientesPaginados(page = page, size = pageSize)
            val pacientes = if (response.isSuccessful && response.body() != null) {
                filtrarPorRol(response.body()!!)
            } else {
                obtenerFakePaginado(page, pageSize)
            }

            val nextKey = if (pacientes.isEmpty() || pacientes.size < pageSize) {
                null
            } else {
                page + 1
            }

            LoadResult.Page(
                data = pacientes,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            val fakeData = obtenerFakePaginado(page, pageSize)
            val nextKey = if (fakeData.isEmpty() || fakeData.size < pageSize) null else page + 1
            LoadResult.Page(
                data = fakeData,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = nextKey
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PacienteResponseDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    private fun filtrarPorRol(lista: List<PacienteResponseDto>): List<PacienteResponseDto> {
        val role = SessionManager.getCurrentRole()
        return when (role) {
            UserRole.EMPLEADO -> lista.filter { it.idPaciente == 1L || it.idPaciente == 4L }
            UserRole.USUARIO -> lista.filter { it.idPaciente == 1L }
            else -> lista
        }
    }

    private fun obtenerFakePaginado(page: Int, size: Int): List<PacienteResponseDto> {
        val all = FakePatientDataSource.getPatientsDto()
        val allFake = filtrarPorRol(all)
        val fromIndex = page * size
        if (fromIndex >= allFake.size) return emptyList()
        val toIndex = minOf(fromIndex + size, allFake.size)
        return allFake.subList(fromIndex, toIndex)
    }
}
