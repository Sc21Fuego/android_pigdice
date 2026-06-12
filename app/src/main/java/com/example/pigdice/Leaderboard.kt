package com.example.pigdice

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val LEADERBOARD_FILE = "leaderboard.txt"
class Leaderboard : AppCompatActivity() {
    var leaderboardList = ArrayList<LeaderboardItem>()
    private lateinit var leaderboardListAdapter : LeaderboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_leaderboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView: RecyclerView = findViewById(R.id.leaderboardRecycler)
        leaderboardListAdapter = LeaderboardAdapter(leaderboardList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = leaderboardListAdapter


        val extras = intent.extras
        if (extras != null) {
//            Retrieve list of records from intent
            val winner = extras.getString("winner")
            val winnerScore = extras.getInt("winnerScore")
            val date = extras.getString("date")
            val leaderboardLogItem = LeaderboardItem(winner!!,winnerScore,date!!)
//            Write records to file
            val fileOutputStream: FileOutputStream = openFileOutput(LEADERBOARD_FILE, MODE_APPEND)
            val leaderboardLogFile = OutputStreamWriter(fileOutputStream)
            leaderboardLogFile.write(leaderboardLogItem.toCSV())
            leaderboardLogFile.close()
        }

        readLeaderboardFileData()
    }
    fun readLeaderboardFileData(){
        val file = File(filesDir, LEADERBOARD_FILE)
        if (file.exists()) {
            file.forEachLine {
                val parts = it.split(",")
                val leaderboardItem = LeaderboardItem(parts[0],parts[1].toInt(),parts[2])
                leaderboardList.add(0, leaderboardItem)
                leaderboardListAdapter.notifyItemInserted(leaderboardList.size)
            }
        }
    }
    fun btnBackOnPress(v: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}