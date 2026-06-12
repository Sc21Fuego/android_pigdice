package com.example.pigdice

import android.widget.ImageView

class Dice(private val diceImageView: ImageView){
    //Dice class to roll dice, save value, and display the appropriate image

    var currentValue: Int = 1

    fun roll(){
        currentValue = if (DEBUG) {
            (2..6).random()
        } else {
            (1..6).random()
        }
        updateImage()
    }
    fun updateImage() {
        // Select a D6 image based on the current value
        val drawableResource = when (currentValue) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
        // Update the ImageView with the selected image
        diceImageView.setImageResource(drawableResource)
    }
}