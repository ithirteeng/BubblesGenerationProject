package com.example.bubblesproject

import com.example.bubblesproject.databinding.ActivityMainBinding
import java.nio.channels.FileLock
import kotlin.math.*

class MainLogic(
    private val screenHeight: Int,
    private val screenWidth: Int,
    private val bubblesArray: ArrayList<Bubble>,
    private val bubbleRadius: Int,
    private val binding: ActivityMainBinding
) {


    fun distanceBetweenBubbles(
        firstX: Float,
        firstY: Float,
        secondX: Float,
        secondY: Float
    ): Double {
        return sqrt((secondX - firstX).toDouble().pow(2.0) + (secondY - firstY).toDouble().pow(2.0))
    }

    fun move() {
        if (bubblesArray.isNotEmpty()) {
            collision()
            for (bubble in bubblesArray) {
                changeDirection(bubble)
                bubble.view.y += getViewY(bubble.speed.toFloat(), bubble.angle)
                bubble.view.x += getViewX(bubble.speed.toFloat(), bubble.angle)
//                Log.e("CORDS", bubble.view.y.toString())
            }

        }
    }

    private fun changeDirection(bubble: Bubble) {
        val x = bubble.view.x
        val y = bubble.view.y
        val angle = anglesToCorrectForm(bubble.angle)

        if (x <= 0f) {
            bubble.angle = correctPhysicsForWalls(angle, 180.0)
        }
        if (y <= 0f) {
            bubble.angle = correctPhysicsForWalls(angle, 90.0)
        }
        if ((x + 2 * bubbleRadius) >= screenWidth.toFloat()) {
            bubble.angle = correctPhysicsForWalls(angle, 0.0)
        }
        if ((y + 2 * bubbleRadius) >= screenHeight.toFloat()) {
            bubble.angle = correctPhysicsForWalls(angle, 270.0)
        }

    }

    private fun correctPhysicsForWalls(firstAngle: Double, secondAngle: Double): Double {
        var angle = 0.0

        if (abs(secondAngle) == 270.0) {
            angle = if (firstAngle > 180.0 && firstAngle < 270.0) {
                firstAngle - 2 * abs(180 - firstAngle)
            } else if (firstAngle > 270.0 && firstAngle < 360.0) {
                abs(360 - firstAngle)
            } else if (angle == 270.0) {
                abs(firstAngle - 180)
            } else {
                firstAngle + 2.0
            }
        }
        if (abs(secondAngle) == 90.0) {
            angle = if (firstAngle > 0.0 && firstAngle < 90.0) {
                abs(360 - firstAngle)
            } else if (firstAngle > 90.0 && firstAngle < 180.0) {
                firstAngle + 2 * abs(180 - firstAngle)
            } else if (firstAngle == 90.0) {
                firstAngle + 180
            } else {
                firstAngle + 2.0
            }
        }
        if (abs(secondAngle) == 0.0) {
            angle = if (firstAngle > 0.0 && firstAngle < 90.0) {
                firstAngle + 2 * abs(90 - firstAngle)
            } else if (firstAngle > 270.0 && firstAngle < 360.0) {
                firstAngle - 2 * abs(270 - firstAngle)
            } else if (firstAngle == 0.0) {
                firstAngle + 180
            } else {
                firstAngle - 2.0
            }
        }
        if (abs(secondAngle) == 180.0) {
            angle = if (firstAngle > 90.0 && firstAngle < 180.0) {
                firstAngle - 2 * abs(90 - firstAngle)
            } else if (firstAngle > 180.0 && firstAngle < 270.0) {
                firstAngle + 2 * abs(270 - firstAngle)
            } else if (firstAngle == 180.0) {
                firstAngle - 180
            } else {
                firstAngle - 2.0
            }
        }

        return anglesToCorrectForm(angle)
    }

    private fun collision() {
        for (i in 0 until bubblesArray.size - 1) {
            for (j in i + 1 until bubblesArray.size) {
                val distance = distanceBetweenBubbles(
                    bubblesArray[i].view.x,
                    bubblesArray[i].view.y,
                    bubblesArray[j].view.x,
                    bubblesArray[j].view.y
                )
                if (distance <= bubbleRadius * 2) {
                    val pair = correctPhysicsForBubblesCollisions(bubblesArray[i], bubblesArray[j])
                    bubblesArray[i].angle = pair.first
                    bubblesArray[j].angle = pair.second
                }
            }
        }
    }

    private fun correctPhysicsForBubblesCollisions(
        firstBubble: Bubble,
        secondBubble: Bubble
    ): Pair<Double, Double> {
        var x1 = firstBubble.view.x
        var y1 = firstBubble.view.y
        var x2 = secondBubble.view.x
        var y2 = secondBubble.view.y


        val touchCords = findTouchCoordinate(x1, y1, x2, y2)
        val cathet = abs(touchCords.first - x1)
        val angle = findRevertAngle(bubbleRadius.toDouble(), abs(cathet).toDouble())

        var firstAngle = firstBubble.angle - angle
        firstAngle - anglesToCorrectForm(firstAngle)
        var secondAngle = secondBubble.angle - angle
        secondAngle = anglesToCorrectForm(secondAngle)


        if (x1 < x2) {
            firstAngle = correctPhysicsForWalls(firstAngle, 0.0) + angle
            secondAngle = correctPhysicsForWalls(secondAngle, 180.0) + angle
            if (y2 > y1) {
                val temp = firstAngle
                firstAngle = secondAngle
                secondAngle = temp
            }
        } else {
            firstAngle = correctPhysicsForWalls(firstAngle, 180.0) + angle
            secondAngle = correctPhysicsForWalls(secondAngle, 0.0) + angle
            if (y1 >= y2) {
                val temp = firstAngle
                firstAngle = secondAngle
                secondAngle = temp
            }


        }


        firstAngle = anglesToCorrectForm(firstAngle)
        secondAngle = anglesToCorrectForm(secondAngle)

        return Pair(firstAngle, secondAngle)
    }

    private fun findTouchCoordinate(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ): Pair<Float, Float> {
        return Pair((x1 + x2) / 2, (y1 + y2) / 2)
    }


    private fun anglesToCorrectForm(angle: Double): Double {
        val temp = if (angle < 0.0) {
            -(angle - angle.toInt()) + abs((360 - angle) % 360)
        } else {
            (angle - angle.toInt()) + abs(angle % 360)
        }
        return temp
    }

    private fun findRevertAngle(hypotenuse: Double, cathet: Double): Double {
        val cosine = cathet / hypotenuse
        val radAngle = acos(cosine)

        return radAngle * 180 / PI
    }

    private fun ifAnglesReverse(firstAngle: Double, secondAngle: Double): Boolean {
        return if (firstAngle in 90.0..270.0) {
            secondAngle in 90.0..270.0
        } else {
            secondAngle <= 90.0 || secondAngle >= 270
        }
    }

    private fun findEndAngle(x1: Float, y1: Float, x2: Float, y2: Float): Double {
        val hypotenuse = distanceBetweenBubbles(x1, y1, x2, y2)
        val cathet = y2 - y1
        val sin = cathet / hypotenuse
        val radAngle = asin(sin)
        return (180 - radAngle * 180 / PI)
    }

    private fun getViewX(speed: Float, directionAngle: Double): Float {
        return speed * cos(angleInRadians(directionAngle))
    }

    private fun getViewY(speed: Float, directionAngle: Double): Float {
        return speed * sin(angleInRadians(directionAngle))
    }


    private fun angleInRadians(angle: Double): Float {
        return ((-angle / 180) * PI).toFloat()
    }
}