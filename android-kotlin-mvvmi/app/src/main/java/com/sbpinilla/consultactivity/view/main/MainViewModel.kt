package com.sbpinilla.consultactivity.view.main

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.sbpinilla.consultactivity.data.dto.EmployeeDTO
import com.sbpinilla.consultactivity.data.dto.request.RequestEmployeeDTO
import com.sbpinilla.consultactivity.data.interactors.IEmployeeInteractor
import com.sbpinilla.consultactivity.di.DaggerEmployeeComponent
import com.sbpinilla.consultactivity.di.EmployeeEntityModule
import com.sbpinilla.consultactivity.di.EmployeeModule
import com.sbpinilla.consultactivity.view.SingleLiveEvent
import io.reactivex.functions.Consumer
import javax.inject.Inject

class MainViewModel : ViewModel() {

    sealed class ViewEvent {

        class ConsultaExitosa(val listEmployees: List<EmployeeDTO>) : ViewEvent()
        class ErrorConsulta(val errorMensaje: String) : ViewEvent()

    }


    var singleLiveEvent: SingleLiveEvent<ViewEvent> =
        SingleLiveEvent()

    @Inject
    lateinit var employeeInteractor: IEmployeeInteractor

    init {
        DaggerEmployeeComponent
            .builder()
            .employeeModule(EmployeeModule())
            .build().inject(this)
    }

    @SuppressLint("CheckResult")
    fun consultarEmployee(requestEmployeeDTO: RequestEmployeeDTO) {

        employeeInteractor.consultarEmployee(requestEmployeeDTO)?.subscribe({

            if (it.size != 0) {
                singleLiveEvent.value = ViewEvent.ConsultaExitosa(it)
            }else{
                singleLiveEvent.value = ViewEvent.ErrorConsulta("No hay datos")
            }

        },{
            //error de consulta
        })


    }

    fun saveInLocal(listEmployees: List<EmployeeDTO>){


        Log.d("MAIN","VIEW MODEL")
        employeeInteractor.saveEmployeeEntityInLocal(listEmployees)

    }


}