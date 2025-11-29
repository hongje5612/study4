package org.example.p19236.misc

interface Fish

class Shark : Fish {}

class Mackerel : Fish {}


fun main() {

    val fish : Fish? = Shark()

    fish as Mackerel



}