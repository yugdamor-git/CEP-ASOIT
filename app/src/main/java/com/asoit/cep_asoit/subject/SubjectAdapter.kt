package com.asoit.cep_asoit.subject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asoit.cep_asoit.R
import java.util.*

class SubjectAdapter(
    val context: Context,
    val pendingQue: ArrayList<Int>
) : RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.subject_recycler_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pendingQue.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.subjectName.text = pendingQue[position].toString()


    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var subjectName: TextView

        init {
            subjectName = view.findViewById(R.id.sub_item_tv)
        }

    }
}