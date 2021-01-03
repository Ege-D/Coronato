package com.ege.coronato.adapters

import com.ege.coronato.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ege.coronato.models.Point
import android.widget.TextView
import kotlin.math.floor


class PointAdapter(val context: Context, val points: ArrayList<Point>): RecyclerView.Adapter<PointAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        private val title = itemView?.findViewById<TextView>(R.id.postListTitleTxt)
        private val body = itemView?.findViewById<TextView>(R.id.postListCommentTxt)
        private val dateAgo = itemView?.findViewById<TextView>(R.id.postListDateAgoTxt)
        fun bindPost(context: Context, point: Point) {
            if (title != null && body != null && dateAgo != null) {
                title.textSize = 12F
                body.textSize = 12F
                dateAgo.textSize = 12F
            }
            title?.text = point.user
            dateAgo?.text = getDateAgo(point.timeStamp)
            body?.text = point.point
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PointAdapter.ViewHolder, position: Int) {
        holder.bindPost(context, points[position])
    }

    override fun getItemCount(): Int {
        return points.count()
    }

    fun getDateAgo(timeStamp: Long?): String? {
        val dateNow = System.currentTimeMillis()
        val dateAgo = (dateNow - timeStamp!!)/1000
        var stringAgo : String? = ""
        when {
            dateAgo < 60 -> {
                stringAgo = "${dateAgo.toString()} saniye önce"
            }
            dateAgo < 3600 -> {
                stringAgo = "${floor(((dateAgo/60).toDouble())).toString().dropLast(2)} dakika önce"
            }
            dateAgo < 86400 -> {
                stringAgo = "${floor(((dateAgo/3600).toDouble())).toString().dropLast(2)} saat önce"
            }
            dateAgo < 604800 -> {
                stringAgo = "${floor(((dateAgo/86400).toDouble())).toString().dropLast(2)} gün önce"
            }
            dateAgo < 2629743.83 -> {
                stringAgo = "${floor(((dateAgo/604800).toDouble())).toString().dropLast(2)} hafta önce"
            }
            dateAgo < 31556926 -> {
                stringAgo = "${floor(((dateAgo/2629743.83).toDouble())).toString().dropLast(2)} ay önce"
            }
            else -> {
                stringAgo = "${floor(((dateAgo/31556926).toDouble())).toString().dropLast(2)} yıl önce"
            }
        }
        return stringAgo
    }
}