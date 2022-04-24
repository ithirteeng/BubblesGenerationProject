package com.example.bubblesproject

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.bubblesproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        const val BUBBLE_WIDTH = 300
        const val BUBBLE_HEIGHT = 300
        const val BUBBLES_AMOUNT = 20
        const val DEFAULT_SPEED = 5
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var bubblesArray: ArrayList<Bubble>
    private lateinit var logic: MainLogic
    private var canBeAdded = true
    private val bubblesColorsArray = arrayListOf(
        "bubble_blue",
        "bubble_pink",
        "bubble_violet"

    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        bubblesArray = arrayListOf()
        setContentView(binding.root)
        clearEvent()

        val screenHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.height()
        } else {
            windowManager.defaultDisplay.height
        }

        val screenWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.width()
        } else {
            windowManager.defaultDisplay.width
        }

        val layoutParams = ConstraintLayout.LayoutParams(BUBBLE_WIDTH, BUBBLE_HEIGHT)
        binding.root.setOnTouchListener { _, motionEvent ->
            createBubble(
                motionEvent,
                layoutParams,
                screenHeight,
                screenWidth
            )
        }
        logic = MainLogic(screenHeight, screenWidth, bubblesArray, BUBBLE_WIDTH / 2, binding)

        moveBubbles()
    }

    private fun clearEvent() {
        binding.bubblesCountView.setOnClickListener {
            clearAll()
        }
    }

    private fun canBeCreated(
        event: MotionEvent,
        bubbleRadius: Int,
        screenHeight: Int,
        screenWidth: Int
    ): Boolean {
        if (event.y - bubbleRadius <= 0 || event.y + bubbleRadius >= screenHeight) {
            return false
        }
        if (event.x - bubbleRadius <= 0 || event.x + bubbleRadius >= screenWidth) {
            return false
        }

        for (bubble in bubblesArray) {
            if (logic.distanceBetweenBubbles(
                    bubble.view.x + bubbleRadius,
                    bubble.view.y + bubbleRadius,
                    event.x,
                    event.y
                ) < 2 * bubbleRadius
            ) {
                return false
            }
        }
        return true
    }

    private fun createBubble(
        motionEvent: MotionEvent,
        layoutParams: ConstraintLayout.LayoutParams,
        screenHeight: Int,
        screenWidth: Int
    ): Boolean {
        if (bubblesArray.size != BUBBLES_AMOUNT) {
            if (canBeCreated(motionEvent, BUBBLE_HEIGHT / 2, screenHeight, screenWidth)) {
                var text = binding.bubblesCountView.text.toString().toInt()
                text++
                binding.bubblesCountView.text = text.toString()

                val x = motionEvent.x
                val y = motionEvent.y

                val image = ImageView(this)
                val color = bubblesColorsArray[(0 until bubblesColorsArray.size).random()]
                image.setImageResource(resources.getIdentifier(color, "drawable", packageName))

                val bubble = Bubble(image, (0..359).random().toDouble(), DEFAULT_SPEED.toDouble())

                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        binding.root.addView(image, layoutParams)
                        image.x = x - BUBBLE_WIDTH / 2
                        image.y = y - BUBBLE_HEIGHT / 2

                    }
                }
                bubblesArray.add(bubble)
            }
        } else {
            if (canBeAdded) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.bubblesError),
                    Toast.LENGTH_SHORT
                ).show()
                canBeAdded = false
            }
        }



        return false
    }

    private fun clearAll() {
        for (bubble in bubblesArray) {
            binding.root.removeView(bubble.view)
        }
        bubblesArray.clear()
        binding.bubblesCountView.text = "0"
        canBeAdded = true
    }

    private fun moveBubbles() {
        object : CountDownTimer(1, 1) {
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
                logic.move()
                moveBubbles()
            }
        }.start()
    }

}
