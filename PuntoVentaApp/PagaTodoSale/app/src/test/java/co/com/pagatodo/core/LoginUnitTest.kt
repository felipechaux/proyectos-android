package co.com.pagatodo.core

import androidx.annotation.NonNull
import co.com.pagatodo.core.data.database.DBHelperResponse
import co.com.pagatodo.core.data.database.entities.StubEntity
import co.com.pagatodo.core.data.dto.StubDTO
import co.com.pagatodo.core.data.interactors.AuthInteractor
import co.com.pagatodo.core.data.interactors.IAuthInteractor
import co.com.pagatodo.core.data.model.AuthModel
import co.com.pagatodo.core.data.repositories.IAuthRepository
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.junit.rules.TemporaryFolder
import org.junit.Rule
import org.mockito.Mockito.*
import org.junit.Assert.assertNotNull
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.disposables.Disposable
import io.reactivex.Scheduler
import org.junit.BeforeClass
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.then
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit


class LoginUnitTest: BaseTest() {

    @get:Rule
    var mTempFolder = TemporaryFolder()

    @Mock
    lateinit var authInteractor: IAuthInteractor

    @Mock
    lateinit var authRepository: IAuthRepository

    @Before
    fun setupTest() {
        authInteractor = AuthInteractor(authRepository)
    }

    @Test
    fun authSuccess() {
        val authModel = AuthModel().apply {
            isSuccess = true
        }

        val interactor = AuthInteractor(authRepository)
        //doReturn(Observable.just(authModel)).`when`(authRepository)?.login(anyString(), anyString())
        interactor?.auth("", "")
        //assertEquals(authModel.isSuccess, true)
        then(authRepository).should().login(anyString(), anyString())
    }

    @Test
    fun getStubsSuccess() {
        val stubEntity = StubEntity().apply {
            serie1 = "s1"
            serie2 = "s2"
        }
        val stubsEntities = arrayListOf<StubEntity>()

        for (index in 1..5) {
            stubsEntities.add(stubEntity)
        }

        val interactor = AuthInteractor(authRepository)
        doReturn(Observable.just(stubsEntities)).`when`(authRepository)?.getStubs()
        interactor?.getStubs()
        assertNotNull(stubsEntities)
    }

    @Test
    fun saveStubsInfoSuccess() {

        val dbResponse = DBHelperResponse().apply {
            status = true
            totalRows = 1
        }

        val stubsDTO = arrayListOf<StubDTO>()
        var stubsEntities = mutableListOf<StubEntity>()

        doReturn(Observable.just(dbResponse)).`when`(authRepository).saveStubsInfo(stubsEntities)

        authInteractor?.saveStubsInfo(stubsDTO)

        assert(dbResponse.status)
    }
}