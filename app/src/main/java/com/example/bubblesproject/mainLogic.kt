package com.example.bubblesproject

import kotlin.math.*

class MainLogic(
    private val screenHeight: Int,
    private val screenWidth: Int,
    private val bubblesArray: ArrayList<Bubble>,
    private val bubbleRadius: Int
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
//                Log.e("CORDS", bubble.view.y.toString())
            }
        }
    }

    private fun check(bubble: Bubble): Boolean {
        if (bubble.view.x <= 0 || bubble.view.y <= 0 || bubble.view.x >= screenWidth || bubble.view.y >= screenHeight) {
            return false
        }
        return true
    }

    private fun changeDirection(bubble: Bubble) {
        val x = bubble.view.x
        val y = bubble.view.y
        var angle = anglesToCorrectForm(bubble.angle)

        if (x <= 0f) {
            bubble.angle = correctPhysicsForBubbles(angle, 180.0, true)
        }
        if (y <= 0f) {
            bubble.angle = correctPhysicsForBubbles(angle, 90.0, true)
        }
        if ((x + 2 * bubbleRadius) >= screenWidth.toFloat()) {
            bubble.angle = correctPhysicsForBubbles(angle, 0.0, true)
        }
        if ((y + 2 * bubbleRadius) >= screenHeight.toFloat()) {
            bubble.angle = correctPhysicsForBubbles(angle, 270.0, true)
        }

    }

    private fun correctPhysicsForBubbles(firstAngle: Double, secondAngle: Double, isWall: Boolean): Double {
        var angle = 0.0
        if (isWall) {
            if (abs(secondAngle) == 270.0) {
                angle = if (firstAngle > 180.0 && firstAngle < 270.0) {
                    firstAngle - 2 * abs(180 - firstAngle)
                } else if (firstAngle > 270.0 && firstAngle < 360.0) {
                    abs(360 - firstAngle)
                } else if (angle == 270.0){
                    abs(firstAngle - 180)
                } else {
                    firstAngle + 1.0
                }
            }
            if (abs(secondAngle) == 90.0) {
                angle = if (firstAngle > 0.0 && firstAngle < 90.0) {
                    abs(360 - firstAngle)
                } else if (firstAngle > 90.0 && firstAngle < 180.0) {
                    firstAngle + 2 * abs(180 - firstAngle)
                } else if (firstAngle == 90.0){
                    firstAngle + 180
                } else {
                    firstAngle  + 1.0
                }
            }
            if (abs(secondAngle) == 0.0) {
                angle = if (firstAngle > 0.0 && firstAngle < 90.0) {
                    firstAngle + 2 * abs(90 - firstAngle)
                } else if (firstAngle > 270.0 && firstAngle < 360.0) {
                    firstAngle - 2 * abs(270 - firstAngle)
                } else if (firstAngle == 0.0){
                    firstAngle + 180
                } else {
                    firstAngle + 1
                }
            }
            if (abs(secondAngle) == 180.0) {
                angle = if (firstAngle > 90.0 && firstAngle < 180.0) {
                    firstAngle - 2 * abs(90 - firstAngle)
                } else if (firstAngle > 180.0 && firstAngle < 270.0) {
                    firstAngle + 2 * abs(270 - firstAngle)
                } else if (firstAngle == 180.0){
                    firstAngle - 180
                } else {
                    firstAngle + 1.0
                }
            }
        }
        else {

        }

        return anglesToCorrectForm(angle)
    }

    private fun anglesToCorrectForm(angle: Double): Double {
        var temp = if (angle < 0.0) {
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

    private fun angleInRadians(angle: Double): Float {
        return ((-angle / 180) * PI).toFloat()
    }
}