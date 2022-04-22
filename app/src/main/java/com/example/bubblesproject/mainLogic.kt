package com.example.bubblesproject

import com.example.bubblesproject.databinding.ActivityMainBinding
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
            for (bubble in bubblesArray) {
                changeDirection(bubble)
                bubble.view.y += getViewY(bubble.speed.toFloat(), bubble.angle)
                bubble.view.x += getViewX(bubble.speed.toFloat(), bubble.angle)
                //bubblesCollision()
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

    private fun correctPhysicsForBubblesCollisions(firstBubble: Bubble, secondBubble: Bubble): Double {
        val x1 = firstBubble.view.x
        val y1 = firstBubble.view.y
        val x2 = secondBubble.view.x
        val y2 = secondBubble.view.y

        val firstCenter = centerCoordinates(x1, y1)
        val secondCenter = centerCoordinates(x2, y2)

        val touchCords = findTouchCoordinate(
            firstCenter.first,
            firstCenter.second,
            secondCenter.first,
            secondCenter.second
        )
        var firstAngle = getAngle(
            touchCords.first,
            touchCords.second,
            firstCenter.first,
            firstCenter.second,
            firstBubble.speed
        )
        var secondAngle = getAngle(
            touchCords.first,
            touchCords.second,
            secondCenter.first,
            secondCenter.second,
            secondBubble.speed
        )

        firstAngle = anglesToCorrectForm(firstAngle)
        secondAngle = anglesToCorrectForm(secondAngle)




        firstAngle = anglesToCorrectForm(firstAngle)
        return firstAngle
    }


    private fun findTouchCoordinate(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ): Pair<Float, Float> {
        return Pair((x1 + x2) / 2, (y1 + y2) / 2)
    }


    private fun centerCoordinates(x: Float, y: Float): Pair<Float, Float> {
        return Pair(x + bubbleRadius, y + bubbleRadius)
    }

    private fun anglesToCorrectForm(angle: Double): Double {
        val temp = if (angle < 0.0) {
            -(angle - angle.toInt()) + abs((360 - angle) % 360)
        } else {
            (angle - angle.toInt()) + abs(angle % 360)
        }
        return temp
    }

    private fun getViewX(speed: Float, directionAngle: Double): Float {
        return speed * cos(angleInRadians(directionAngle))
    }

    private fun getViewY(speed: Float, directionAngle: Double): Float {
        return speed * sin(angleInRadians(directionAngle))
    }


    private fun getAngle(touchX: Float, touchY: Float, x: Float, y: Float, speed: Double): Double {
        val cosine = distanceBetweenBubbles(touchX, touchY, x, y) / speed
        val angle = acos(cosine)
        return angle * 180.0 / PI
    }

    private fun angleInRadians(angle: Double): Float {
        return ((-angle / 180) * PI).toFloat()
    }
}