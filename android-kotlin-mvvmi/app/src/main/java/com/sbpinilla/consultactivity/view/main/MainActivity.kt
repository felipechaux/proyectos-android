package com.sbpinilla.consultactivity.view.main

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaredrummler.materialspinner.MaterialSpinner

import com.sbpinilla.consultactivity.R
import com.sbpinilla.consultactivity.data.database.DatabaseViewModel
import com.sbpinilla.consultactivity.data.dto.EmployeeDTO
import com.sbpinilla.consultactivity.data.dto.request.RequestEmployeeDTO
import com.sbpinilla.consultactivity.view.adapters.EmployeeAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private lateinit var employeeViewModel: MainViewModel

    lateinit var option: MaterialSpinner

    private var subscriptions = arrayListOf<Disposable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewModel()
        setupView()
    }


    private fun setupViewModel() {

       // val options= arrayOf("list1","list2","list3")

        //items.add(2,"hola3");
        //option=findViewById(R.id.spin) as MaterialSpinner

       // option.setAdapter(ArrayAdapter<String>(applicationContext,android.R.layout.simple_list_item_1,options))


       // spin.setItems(items)

        employeeViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        employeeViewModel.singleLiveEvent.observe(this, Observer {

            when (it) {
                is MainViewModel.ViewEvent.ErrorConsulta -> {
                    Toast.makeText(baseContext, "Error ${it.errorMensaje}", Toast.LENGTH_LONG).show()

                }
                is MainViewModel.ViewEvent.ConsultaExitosa -> {


                    Toast.makeText(
                        baseContext,
                        "Nombre del primer empleado: ${it.listEmployees.first().employeeName} y su ID ${it.listEmployees.first().id}",
                        Toast.LENGTH_LONG
                    ).show()


                    updateList(it.listEmployees)



                    employeeViewModel.saveInLocal(it.listEmployees)



                    //Toast.makeText(baseContext, "La cantidad de resultados es  ${it.listEmployees.size}", Toast.LENGTH_LONG).show()
                }
            }

        })

        employeeViewModel.consultarEmployee(RequestEmployeeDTO())

        buttonSend.setOnClickListener {

            Toast.makeText(baseContext, "Enviar"+" "+txtNombre.text, Toast.LENGTH_LONG).show()

        }



    }

    private fun updateList(listEmployees: List<EmployeeDTO>) {


        rv_list.adapter = EmployeeAdapter(listEmployees, this)

        val items = mutableListOf<String>()
        items.add("Selecciona algo")
        for (item in listEmployees) {
            items.add(item.employeeAge.toString())
        }
        spin.setItems(items)


    }

    private fun setupView() {

        rv_list.layoutManager = LinearLayoutManager(this)

    }

    override fun onResume() {
        super.onResume()

        subscriptions.add(DatabaseViewModel.database
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                when (it) {
                    DatabaseViewModel.DatabaseEvents.EMPLOYEE_CREATE -> showMsm()
                }

            }
        )

    }

    override fun onPause() {
        super.onPause()

        subscriptions.forEach {
            it.dispose()
        }

    }

    private fun showMsm() {

        Toast.makeText(baseContext, "create items in BD", Toast.LENGTH_LONG).show()

        Log.d("MAIN","SAVE IN BD")

    }

}
