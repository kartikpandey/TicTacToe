package com.dheeraj.tictactoe

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(val context: Context) {

    private val sharedPrefFile = "TicTacToeShrdPref"

    var sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

    fun checkSavedGame(): Boolean = sharedPreferences.getString("player1Name", "")?.isEmpty()!!

    fun saveProgress(player1: Player, player2: Player) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("player1Name", player1.name)
        editor.putString("player2Name", player2.name)
        editor.putInt("player1Points", player1.score)
        editor.putInt("player2Points", player2.score)
        editor.apply()
        editor.commit()
    }

    fun retrieveProgress() : Pair<Player, Player> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val name_p1 = sharedPreferences.getString("player1Name", "")
        val name_p2 = sharedPreferences.getString("player2Name", "")
        val score1 = sharedPreferences.getInt("player1Points", 0)
        val score2 = sharedPreferences.getInt("player2Points", 0)
        return Pair(Player(score1, name_p1), Player(score2, name_p2))
    }

    fun clearPreferences() {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}