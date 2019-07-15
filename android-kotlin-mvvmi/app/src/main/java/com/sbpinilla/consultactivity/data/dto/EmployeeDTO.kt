package com.sbpinilla.consultactivity.data.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class EmployeeDTO {


    @SerializedName("id")
    var id: String? = null

    @SerializedName("employee_name")
    var employeeName: String? = null

    @SerializedName("employee_salary")
    var employeeSalary: String? = null

    @SerializedName("employee_age")
    var employeeAge: String? = null

    @SerializedName("profile_image")
    var profileImage: String? = null

}