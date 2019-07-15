package co.com.pagatodo.core.network

import com.sbpinilla.consultactivity.data.dto.EmployeeDTO
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Interfaz usada para definir los metodos de cada servicio, en ellos se le asigna una etiqueta con el tipo de llamado al servicio
 * y tambien el endpoint perteneciente a ese servicio, en el metodo se asigna como parametro de entrada el request como DTO
 * y como variable de retorno un observable que contiene el DTO de respuesta.
 */
interface ConsultaService {


    @GET("employees")
    fun searchEmployees(): Observable<List<EmployeeDTO>>


}