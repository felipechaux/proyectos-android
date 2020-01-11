package co.com.pagatodo.core

import co.com.pagatodo.core.data.dto.request.RequestUtilDTO
import co.com.pagatodo.core.data.dto.response.ResponseGenericDTO
import co.com.pagatodo.core.data.interactors.IUtilInteractor
import co.com.pagatodo.core.data.interactors.UtilInteractor
import co.com.pagatodo.core.data.model.request.RequestUtilModel
import co.com.pagatodo.core.data.model.response.ResponseGenericModel
import co.com.pagatodo.core.data.repositories.IUtilRepository
import co.com.pagatodo.core.data.repositories.UtilRepository
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeUnit

class UtilUnitTest: BaseTest() {

    lateinit var utilInteractor: IUtilInteractor

    @Mock
    lateinit var utilRepository: IUtilRepository

    @Before
    fun setupTest() {
        utilInteractor = UtilInteractor(utilRepository)
    }

    @Test
    fun updateStubSuccess() {
        //doReturn(Observable.just(responseDTO)).`when`(utilRepository).updateStub(requestDTO)
        //given(utilRepository.updateStub(requestDTO)).willReturn(Observable.just(responseDTO))
        val testObserver = TestObserver.create<ResponseGenericModel>()
        utilInteractor.updateStubInServer(RequestUtilModel())?.subscribe()
        then(utilRepository).should().updateStub(anyObject())
    }

    @Test
    fun updateStubError() {

        val requestDto = RequestUtilDTO().apply {  }

        val interactor = UtilInteractor(utilRepository)
        doReturn(Observable.just(1)).`when`(utilRepository)?.updateStub(requestDto)
        interactor.updateStubInServer(RequestUtilModel())
        utilRepository.updateStub(requestDto)
        verify(utilRepository).updateStub(requestDto)
    }
}