package com.example.pigdice

// Single LogItem - String
class LeaderboardItem (var winner:String, var points:Int, var date:String){
    fun toCSV() : String {
        return "$winner,$points,$date\n"
    }
    override fun toString(): String{
        return winner.padEnd(10) + points.toString().padEnd(5) + date
    }
}