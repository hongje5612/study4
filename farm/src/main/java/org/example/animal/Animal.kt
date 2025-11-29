package org.example.animal

import org.example.food.Food
import org.example.misc.Direction

interface Animal {
    var health : Int
    var direction : Direction

    fun eat(food : Food) {
        health += food.nutrient
    }

    fun walk() {
        health -= 2
    }
}