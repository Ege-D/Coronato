package com.ege.coronato.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ege.coronato.R
import com.ege.coronato.controller.BusinessActivity
import com.ege.coronato.models.Result

class SearchAdapter (val context: Context, val results: ArrayList<Result>): RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)  {
        private val title = itemView?.findViewById<TextView>(R.id.searchListTxt)
        fun bindPost(context: Context, result: Result) {
            title?.text = result.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.search_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindPost(context, results[position])
        holder.itemView.setOnClickListener {
            val profileIntent = Intent(context, BusinessActivity::class.java)
            profileIntent.putExtra("businessID", results[position].id)
            context.startActivity(profileIntent)
        }
    }

    override fun getItemCount(): Int {
        return results.count()
    }
}