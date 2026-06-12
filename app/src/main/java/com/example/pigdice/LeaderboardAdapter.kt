package com.example.pigdice

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

internal class LeaderboardAdapter (private var leaderboardLogList: List<LeaderboardItem>) :
    RecyclerView.Adapter<LeaderboardAdapter.MyViewHolder>(){
        internal class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
            var item : TextView = view.findViewById(R.id.leaderboardItemText)
    }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       val itemView = LayoutInflater.from(parent.context).inflate(R.layout.leaderboard_item,parent,false)
       return MyViewHolder(itemView)
   }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val leaderboardLogItem = leaderboardLogList[position]
        holder.item.text = leaderboardLogItem.toString()
        holder.item.typeface = (Typeface.MONOSPACE)
        if (leaderboardLogItem.winner == PLAYER_NAME){
            holder.item.setTextColor(Color.BLUE)
        } else {
            holder.item.setTextColor(Color.RED)
        }
    }

    override fun getItemCount(): Int {
        return leaderboardLogList.size
    }
}