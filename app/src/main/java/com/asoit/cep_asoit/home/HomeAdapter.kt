package com.asoit.cep_asoit.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.asoit.cep_asoit.R
import com.asoit.cep_asoit.subject.subject
import java.util.*

class HomeAdapter(
    val context: Context,
    val subjects: ArrayList<subject>
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.home_adapter_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return subjects.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.subjectName.text = subjects[position].name
        holder.subjectName.setOnClickListener {

            val action = HomeFragmentDirections.actionHomeFragmentToSubjectFragment(
                subjects[position].name,
                subjects[position].id
            )
            it.findNavController().navigate(action)
        }


    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var subjectName: TextView

        init {
            subjectName = view.findViewById(R.id.tv_subject_name)
        }

    }
}