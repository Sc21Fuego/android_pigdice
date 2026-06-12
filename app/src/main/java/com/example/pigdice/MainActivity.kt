package com.example.pigdice

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*
GAME LOGIC:
    - If a 1 is rolled on one die, the turn score goes to 0 and the turn is given to the other player
    - If a 1 is rolled on both dice, the total score goes to 0 and the turn is given to the other player
    - All other rolls are added together and added to the turn score
    - Once the "Hold" button is pressed, the turn score is added to the total score and the turn goes to the other player.

    Play passes from player to player until a winner is determined.  A winner is the first one to reach 100 points.

    We are playing against the computer that will roll up to 3 times during the computer's turn.
    The same rules about throwing a 1 apply to the computer.
    If all 3 rolls are thrown without a 1, then the 3 turns totals are added to computer's grand total.
 */

//    Set Constant for score limit per round
const val SCORE_LIMIT = 100

const val PLAYER_NAME = "Dave"
const val COMPUTER_NAME = "HAL9000"


class MainActivity : AppCompatActivity() {
// Lateinit UI variables; initialization through initApplication() during OnCreate()
    lateinit var imgWin: ImageView
    lateinit var imgLose: ImageView
    lateinit var imgDice1: ImageView
    lateinit var imgDice2: ImageView
    lateinit var imgLeftArrow: ImageView
    lateinit var imgRightArrow: ImageView
    lateinit var txtPlayerName: TextView
    lateinit var txtCompName: TextView
    lateinit var txtPlayerGamesWon: TextView
    lateinit var txtCompGamesWon: TextView
    lateinit var txtPlayerTotalScore: TextView
    lateinit var txtCompTotalScore: TextView
    lateinit var txtPlayerTurnTotal: TextView
    lateinit var txtCompTurnTotal: TextView
    lateinit var txtDiceTotal: TextView
    lateinit var btnRoll: Button
    lateinit var btnHold: Button
    lateinit var btnLeaderboard: Button
    // Put the dice into a class to separate duties of rolling & value tracking from main program
    lateinit var dice1: Dice
    lateinit var dice2: Dice

//    Track numeric variables to update text from various function calls
    var playerGamesWon = 0
    var compGamesWon = 0
    var playerTotalScore = 0
    var compTotalScore = 0
    var playerTurnTotal = 0
    var compTurnTotal = 0
    var diceTotal = 0
    var winner : String? = null
    var winnerScore : Int? = null
    companion object {
        var debug: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initApplication()
    }
    override fun onResume(){
        super.onResume()
//        Catch-all to prevent duplicate leaderboard log entries
        clearStalePlayerData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        Save scores
        outState.putInt("playerGamesWon", playerGamesWon)
        outState.putInt("compGamesWon", compGamesWon)
        outState.putInt("playerTotalScore", playerTotalScore)
        outState.putInt("compTotalScore", compTotalScore)
        outState.putInt("playerTurnTotal", playerTurnTotal)
        outState.putInt("compTurnTotal", compTurnTotal)
//        Save dice
        outState.putInt("dice1CurrentValue", dice1.currentValue)
        outState.putInt("dice2CurrentValue", dice2.currentValue)
        outState.putInt("diceTotal", diceTotal)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
//        Restore saved scores
        playerGamesWon = savedInstanceState.getInt("playerGamesWon")
        compGamesWon = savedInstanceState.getInt("compGamesWon")
        playerTotalScore = savedInstanceState.getInt("playerTotalScore")
        compTotalScore = savedInstanceState.getInt("compTotalScore")
        playerTurnTotal = savedInstanceState.getInt("playerTurnTotal")
        txtPlayerGamesWon.text = playerGamesWon.toString()
        txtCompGamesWon.text = compGamesWon.toString()
        txtPlayerTotalScore.text = playerTotalScore.toString()
        txtCompTotalScore.text = compTotalScore.toString()
        txtPlayerTurnTotal.text = playerTurnTotal.toString()
        txtCompTurnTotal.text = compTurnTotal.toString()

//        Restore dice
        diceTotal = savedInstanceState.getInt("diceTotal")
        dice1.currentValue = savedInstanceState.getInt("dice1CurrentValue")
        dice2.currentValue = savedInstanceState.getInt("dice2CurrentValue")
        updateDice()
    }

    private fun initApplication(){
//        Init win/loss graphics
        imgWin = findViewById(R.id.imgWin)
        imgWin.setOnClickListener { // Set image to disappear from view & layout when clicked
            imgWin.visibility = View.INVISIBLE
            btnLeaderboardOnPress( findViewById(R.id.imgWin))
        }
        imgLose = findViewById(R.id.imgLose)
        imgLose.setOnClickListener{ // Set image to disappear from view & layout when clicked
            imgLose.visibility = View.INVISIBLE
            btnLeaderboardOnPress( findViewById(R.id.imgLose))
        }
//        Init dice
        imgDice1 = findViewById(R.id.imgDice1)
        imgDice2 = findViewById(R.id.imgDice2)
        dice1 = Dice(imgDice1)
        dice2 = Dice(imgDice2)

//        Init remaining graphics
        imgLeftArrow = findViewById(R.id.imgLeftArrow)
        imgRightArrow = findViewById(R.id.imgRightArrow)
        txtPlayerName = findViewById(R.id.txtPlayerName)
        txtCompName = findViewById(R.id.txtCompName)
        txtPlayerGamesWon = findViewById(R.id.txtPlayerGamesWon)
        txtCompGamesWon = findViewById(R.id.txtCompGamesWon)
        txtPlayerTotalScore = findViewById(R.id.txtPlayerTotalScore)
        txtCompTotalScore = findViewById(R.id.txtCompTotalScore)
        txtPlayerTurnTotal = findViewById(R.id.txtPlayerTurnTotal)
        txtCompTurnTotal = findViewById(R.id.txtCompTurnTotal)
        txtDiceTotal = findViewById(R.id.txtDiceTotal)
        btnHold = findViewById(R.id.btnHold)
        btnRoll = findViewById(R.id.btnRoll)
        btnLeaderboard = findViewById(R.id.btnLeaderboard)

//        triple-click computer opponent name to activate debug mode - no ones & much faster comp turns
        setupTripleClick(txtCompName)
    }

    private fun clearStalePlayerData() {
//        Nullify any winner or winnerScore records
        winner = null
        winnerScore = null
    }

    fun showWin(){
        //    Shows Win graphic
        imgWin.visibility = View.VISIBLE
    }


    fun showLose(){
        //    Shows Lose graphic
        imgLose.visibility = View.VISIBLE
    }

    fun checkRollForOnes() : Int {
//        Checks that no "1" values are currently showing on the dice
        return if (diceTotal == 2) {
            2
        } else if (dice1.currentValue == 1 || dice2.currentValue == 1) {
            1
        } else {
            0
        }
    }

    fun rollDice() {
        //    Rolls both dice and updates diceTotal & txtDiceTotal to match
        dice1.roll()
        dice2.roll()
        diceTotal = dice1.currentValue + dice2.currentValue
        when (checkRollForOnes()) {
            0 -> txtDiceTotal.text = diceTotal.toString()
            1 -> txtDiceTotal.text = getString(R.string.bust)
            2 -> txtDiceTotal.text = getString(R.string.snake_eyes)
        }
    }

    fun updateDice() {
//        Update the dice images and total to match the curren value, used during app transitions
        dice1.updateImage()
        dice2.updateImage()
        when (checkRollForOnes()) {
            0 -> txtDiceTotal.text = diceTotal.toString()
            1 -> txtDiceTotal.text = getString(R.string.bust)
            2 -> txtDiceTotal.text = getString(R.string.snake_eyes)
        }
    }

    fun checkForVictoryCondition(): Boolean {
//        Checks if either player or computer total + turn score has exceeded SCORE_LIMIT; Returns inverse for simpler implementation
        return (playerTurnTotal + playerTotalScore >= SCORE_LIMIT || compTurnTotal + compTotalScore >= SCORE_LIMIT)
    }

    fun resetScores(){
        playerTurnTotal = 0
        compTurnTotal = 0
        playerTotalScore = 0
        compTotalScore = 0
        txtPlayerTurnTotal.text = playerTurnTotal.toString()
        txtCompTurnTotal.text = compTurnTotal.toString()
        txtPlayerTotalScore.text = playerTotalScore.toString()
        txtCompTotalScore.text = compTotalScore.toString()
    }
    fun btnRollOnPress(v: View){
//        Logic when player presses "ROLL" button
        rollDice()
        when (checkRollForOnes()){
            1 -> {
                playerTurnTotal = 0
                txtPlayerTurnTotal.text = playerTurnTotal.toString()
//            Rolling a 1 will automatically initiate computer turn
                compTurn()
            }
            2 -> {
                playerTurnTotal = 0
                txtPlayerTurnTotal.text = playerTurnTotal.toString()
                playerTotalScore = 0
                txtPlayerTotalScore.text = playerTotalScore.toString()
                compTurn()
            }
            else -> {
                playerTurnTotal += diceTotal
                txtPlayerTurnTotal.text = playerTurnTotal.toString()
            }
        }
    }

    fun btnHoldOnPress(v: View){
//        Hold button checks for win, initiates computer turn if no win
        playerTurnEnd()
        if (checkForVictoryCondition()){
            playerGamesWon++
            txtPlayerGamesWon.text = playerGamesWon.toString()
            winner = PLAYER_NAME
            winnerScore = playerTotalScore
            resetScores()
            showWin()
        } else {
            compTurn()
        }
    }

    fun btnLeaderboardOnPress(v: View){
        val intent = Intent(this, Leaderboard::class.java)
        if (winner != null) {
            intent.putExtra("winner", winner)
            intent.putExtra("winnerScore", winnerScore)
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val formattedDate = LocalDateTime.now().format( formatter )
            intent.putExtra("date", formattedDate)
        }
        startActivity(intent)
    }

    fun playerTurnEnd(){
//        When player's turn ends, diable "ROLL" button, update player total score, and zero out player turn score
        playerTotalScore += playerTurnTotal
        txtPlayerTotalScore.text = playerTotalScore.toString()
        playerTurnTotal = 0
        txtPlayerTurnTotal.text = playerTurnTotal.toString()
    }

    fun compTurn(){
        btnRoll.isEnabled = false
        btnHold.isEnabled = false
//        Determine computer turn time length based on debug mode
        var totalTime : Long
        var stepTime : Long
        if (debug) {
            totalTime = 3000
            stepTime = 100
        } else {
            totalTime = 4500
            stepTime = 1500
        }
//        Delay start of computer turn so that player can first briefly review their last roll & updated total score
        Handler(Looper.getMainLooper()).postDelayed({
            imgLeftArrow.visibility = View.INVISIBLE
            imgRightArrow.visibility = View.VISIBLE


            object: CountDownTimer(totalTime, stepTime){
//                Computer attempts to roll 3 times. Cancels remaining rolls if it reaches victory condition or rolls a 1
                override fun onTick(p0: Long) {
                    rollDice()
                    if (checkForVictoryCondition()){
                        this.cancel()
                        this.onFinish()
                    } else when (checkRollForOnes()){
                        1 -> {
                            compTurnTotal = 0
                            txtCompTurnTotal.text = compTurnTotal.toString()
                            this.cancel()
                            this.onFinish()
                        }
                        2 -> {
                            compTurnTotal = 0
                            txtCompTurnTotal.text = compTurnTotal.toString()
                            compTotalScore = 0
                            txtCompTotalScore.text = compTotalScore.toString()
                            this.cancel()
                            this.onFinish()
                        }
                        else -> {
                            compTurnTotal += diceTotal
                            txtCompTurnTotal.text = compTurnTotal.toString()
                        }
                    }
                }

                override fun onFinish() {
                    if (checkForVictoryCondition()){
                        compGamesWon++
                        txtCompGamesWon.text = compGamesWon.toString()
                        compTotalScore += compTurnTotal
                        winner = COMPUTER_NAME
                        winnerScore = compTotalScore
                        showLose()
                        resetScores()
                    } else {
                        compTotalScore += compTurnTotal
                        txtCompTotalScore.text = compTotalScore.toString()
                        compTurnTotal = 0
                        txtCompTurnTotal.text = compTurnTotal.toString()
                    }
                    btnRoll.isEnabled = true
                    btnHold.isEnabled = true
                    imgRightArrow.visibility = View.INVISIBLE
                    imgLeftArrow.visibility = View.VISIBLE
                }
            }.start()
        }, stepTime)
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun setupTripleClick(view: View) {
        var tapCount = 0
        var lastTapTimeMs: Long = 0

        // Timeout allowed between individual taps to be considered a multi-tap gesture
        val doubleTapLimit = ViewConfiguration.getDoubleTapTimeout().toLong()

        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    val currentTime = System.currentTimeMillis()

                    // If time gap between taps is within limits, increment count. Otherwise, reset.
                    if (currentTime - lastTapTimeMs < doubleTapLimit) {
                        tapCount++
                    } else {
                        tapCount = 1
                    }

                    lastTapTimeMs = currentTime

                    if (tapCount == 3) {
                        onTripleClicked()
                        tapCount = 0 // Reset after triggering
                    }
                }
            }
            true
        }
    }

    private fun onTripleClicked() {
        Toast.makeText(this, "Debug mode activated", Toast.LENGTH_SHORT).show()
        debug = !debug
    }
}


