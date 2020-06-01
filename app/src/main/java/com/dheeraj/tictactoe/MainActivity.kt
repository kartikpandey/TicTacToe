package com.dheeraj.tictactoe

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , View.OnClickListener {

    private val buttons = Array(3) { arrayOfNulls<Button>(3) }
    private var player1Turn = true
    private var roundCount = 0
    private var player1 = Player(0, "")
    private var player2 = Player(0, "")
    private var shrdPref: SharedPrefs? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shrdPref = SharedPrefs(this)
        if (shrdPref!!.checkSavedGame()) {
            getPlayersName()
        } else {
            restoreGame()
        }
        for (i in 0..2) {
            for (j in 0..2) {
                val buttonID = "button_$i$j"
                val resID = resources.getIdentifier(buttonID, "id", packageName)
                buttons[i][j] = findViewById(resID)
                buttons[i][j]?.setOnClickListener(this)
                buttons[i][j]?.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.buttonColor))
            }
        }
        button_reset.setOnClickListener { resetConfirmation() }
    }

    private fun restoreGame() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Restore Game")
        builder.setMessage("Saved game found!\nWant to restore that game or play new game?")
        builder.setPositiveButton(getString(R.string.restore)) { dialog, which ->
            dialog.cancel()
            val (player1, player2) = shrdPref!!.retrieveProgress()
            this.player1 = player1
            this.player2 = player2
            player1.name = name_p1.text.toString()
            player2.name = name_p2.text.toString()

            score_p1!!.text = player1.score.toString()
            score_p2!!.text = player2.score.toString()
            parent_layout.visibility = View.VISIBLE
        }
        builder.setNeutralButton(getString(R.string.new_game)) { dialog, which ->
            dialog.cancel()
            getPlayersName()
            shrdPref!!.clearPreferences()
        }
        builder.show()
    }

    private fun getPlayersName() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.players_info_layout)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val button_play = dialog.findViewById(R.id.button_play) as Button
        val et_p1_name = dialog.findViewById(R.id.et_p1_name) as EditText
        val et_p2_name = dialog.findViewById(R.id.et_p2_name) as EditText

        button_play.setOnClickListener {
            name_p1.text = et_p1_name.text.toString()
            name_p2.text = et_p2_name.text.toString()

            player1.name = name_p1.text.toString()
            player2.name = name_p2.text.toString()

            if (player1.name!!.isEmpty() || player1.name == " ") {
                name_p1.error = "Enter Player 1 Name"
            } else if (player2.name!!.isEmpty() || player2.name == " ") {
                name_p2.error = "Enter Player 2 Name"
            } else {
                parent_layout.visibility = View.VISIBLE
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun resetConfirmation() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you Sure?")
        builder.setMessage("Game progress will be cleared")
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            resetGame()
            dialog.cancel()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

    override fun onClick(view: View) {
        if ((view as Button).text.toString() != "") {
            return
        }
        if (player1Turn) view.text = "X" else view.text = "O"
        roundCount++
        when {
            checkForWin() -> {
                Handler().postDelayed({
                    if (player1Turn) {
                        player1Wins()
                    } else {
                        player2Wins()
                    }
                }, 1000)
            }

            roundCount == 9 -> draw()

            else -> player1Turn = !player1Turn
        }
    }

    private fun checkForWin(): Boolean {
        val field = Array(3) { arrayOfNulls<String>(3) }
        for (i in 0..2) {
            for (j in 0..2) {
                field[i][j] = buttons[i][j]!!.text.toString()
            }
        }
        for (i in 0..2) {
            if (field[i][0] == field[i][1] && field[i][0] == field[i][2] && field[i][0] != "") {
                showMatchings(buttons[i][0], buttons[i][1], buttons[i][2])
                return true
            }
        }
        for (i in 0..2) {
            if (field[0][i] == field[1][i] && field[0][i] == field[2][i] && field[0][i] != "") {
                showMatchings(buttons[0][i], buttons[1][i], buttons[2][i])
                return true
            }
        }
        if (field[0][0] == field[1][1] && field[0][0] == field[2][2] && field[0][0] != "") {
            showMatchings(buttons[0][0], buttons[1][1], buttons[2][2])
            return true
        }
        if (field[0][2] == field[1][1] && field[0][2] == field[2][0] && field[0][2] != "") {
            showMatchings(buttons[0][2], buttons[1][1], buttons[2][0])
            return true
        }
        return false
    }

    private fun player1Wins() {
        player1.score++
        Toast.makeText(this, "${player1.name} wins this round!", Toast.LENGTH_SHORT).show()
        updatePointsText()
        resetBoard()
    }

    private fun player2Wins() {
        player2.score++
        Toast.makeText(this, "${player2.name} wins this round!", Toast.LENGTH_SHORT).show()
        updatePointsText()
        resetBoard()
    }

    private fun draw() {
        Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show()
        resetBoard()
    }

    @SuppressLint("SetTextI18n")
    private fun updatePointsText() {
        score_p1!!.text = player1.score.toString()
        score_p2!!.text = player2.score.toString()
    }

    private fun resetBoard() {
        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j]!!.text = ""
                buttons[i][j]!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.buttonColor))
            }
        }
        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j]!!.isEnabled = true
            }
        }
        roundCount = 0
        player1Turn = true

        when {
            player1.score == 10 -> showWinMessage("${name_p1.text} wins the game!")
            player2.score == 10 -> showWinMessage("${name_p2.text} wins the game!")
        }
    }

    private fun resetGame() {
        player1.score = 0
        player2.score = 0
        updatePointsText()
        resetBoard()
        shrdPref!!.clearPreferences()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("roundCount", roundCount)
        outState.putInt("player1Points", player1.score)
        outState.putInt("player2Points", player2.score)
        outState.putBoolean("player1Turn", player1Turn)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        roundCount = savedInstanceState.getInt("roundCount")
        player1.score = savedInstanceState.getInt("player1Points")
        player2.score = savedInstanceState.getInt("player2Points")
        player1Turn = savedInstanceState.getBoolean("player1Turn")
    }

    private fun showMatchings(button: Button?, button1: Button?, button2: Button?) {
        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j]!!.isEnabled = false
            }
        }
        button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.highlightColor))
        button1!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.highlightColor))
        button2!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.highlightColor))
    }

    override fun onBackPressed() {
        if ((player1.score != 10 && player2.score != 10)) {
            if (player1.score != 0 || player2.score != 0) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("TicTacToe")
                builder.setMessage("Do you want to current save progress?")
                builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
                    shrdPref!!.saveProgress(player1, player2)
                    dialog.dismiss()
                    finish()
                }

                builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
                    dialog.dismiss()
                    finish()
                }
                builder.show()
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun showWinMessage(msg: String, title: String = "TicTacToe") {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setCancelable(false)
        builder.setNeutralButton(getString(R.string.ok)) { dialog, which ->
            dialog.cancel()
            shrdPref!!.clearPreferences()
            resetGame()
        }
        builder.show()
    }
}
