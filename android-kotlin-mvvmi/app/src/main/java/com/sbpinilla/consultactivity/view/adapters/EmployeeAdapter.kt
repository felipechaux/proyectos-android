package com.sbpinilla.consultactivity.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sbpinilla.consultactivity.R
import com.sbpinilla.consultactivity.data.dto.EmployeeDTO
import kotlinx.android.synthetic.main.item_employee.view.*

class EmployeeAdapter(val items : List<EmployeeDTO>, val context: Context) : RecyclerView.Adapter<EmployeeViewHolder>() {



    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        return EmployeeViewHolder(LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {

        val employee = items.get(position)

        holder?.view_id?.text ="Id: "+employee.id
        holder?.view_name?.text ="Name: "+employee.employeeName
        holder?.view_age?.text ="Age: "+employee.employeeAge


    }
}

class EmployeeViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val view_id = view.txt_id
    val view_name= view.txt_nombre
    val view_age=view.txt_age
}