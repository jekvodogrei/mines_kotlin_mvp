package com.example.mines_kotlin_mvp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.os.Handler
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class MainActivity : AppCompatActivity(), MineSweeperView.OnScoreChangeListener, MineSweeperView.OnGameEndListener {


    private lateinit var mineSweeperView: MineSweeperView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var scoreTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var highScoreTextView: TextView

    private var elapsedTime = 0
    private var isTimerRunning = false
    private val timerHandler = Handler()
    private var highScore = 0

    private val timerRunnable = object : Runnable {
        override fun run() {
            elapsedTime++
            timerTextView.text = String.format(getString(R.string.time), elapsedTime)
            timerHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get a reference to the TextView
       highScoreTextView = findViewById(R.id.tvHighScore)

        // Download high score
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        highScore = sharedPrefs.getInt("highScore", 0)

        // Determination of the number of field cells
       highScoreTextView.text = String.format(getString(R.string.high_score_text), highScore)
        mineSweeperView = findViewById(R.id.mineSweeperView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        scoreTextView = findViewById(R.id.tvScore)
        scoreTextView.text = getString(R.string.score, 0)
        timerTextView = findViewById(R.id.tvTimer)

        swipeRefreshLayout.setOnRefreshListener {
            resetGame()
            swipeRefreshLayout.isRefreshing = false
        }

        // listener instead of scoreUpdateListener
        mineSweeperView.setOnScoreChangeListener(this)

        if (!isTimerRunning) {
            timerHandler.post(timerRunnable)
            isTimerRunning = true
        }

        mineSweeperView.setOnGameEndListener(this)
    }

    private fun resetGame() {
        mineSweeperView.resetGame()

        // redraw the board screen
        mineSweeperView.invalidate()

        elapsedTime = 0
        timerTextView.text = String.format(getString(R.string.time), elapsedTime)
        if (!isTimerRunning) {
            timerHandler.post(timerRunnable)
            isTimerRunning = true
        }
    }

    // Implementation of the onScoreChanged() method of the OnScoreChangeListener interface
    override fun onScoreChanged(score: Int) {
        scoreTextView.text = getString(R.string.score, score)

    }

    override fun onDestroy() {
        super.onDestroy()
        timerHandler.removeCallbacks(timerRunnable)
    }

    override fun onGameEnd() {
        isTimerRunning = false
        timerHandler.removeCallbacks(timerRunnable)

        // Keeps a high score if passed
       val score = mineSweeperView.getCurrentScore()
        if (score > highScore) {
            highScore = score
            val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().putInt("highScore", highScore).apply()

           highScoreTextView.text = String.format(getString(R.string.high_score_text), highScore)

        }
    }

}
