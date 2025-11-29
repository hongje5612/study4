package org.example.operator


class Meter(var length : Double) {

    operator fun plus(other : Meter) : Meter {
        return Meter(length + other.length)
    }

    operator fun minus(other : Meter) : Meter {
        return Meter(length - other.length)
    }

    operator fun times(m : Double) : Meter {
        return Meter(length * m)
    }

    operator fun div(m : Double) : Meter {
        return Meter(length / m)
    }

    operator fun rem(m : Double) : Meter {
        return Meter(length % m)
    }

    operator fun plusAssign(other : Meter) {
        length += other.length
    }

    operator fun minusAssign(other : Meter) {
        length -= other.length
    }

    operator fun timesAssign(m : Double) {
        length *= m
    }

    operator fun divAssign(m : Double) {
        length /= m
    }

    operator fun remAssign(m : Double) {
        length %= m
    }

    operator fun compareTo(other: Meter) : Int  {
        if(length > other.length) return 1
        else if(length == other.length) return 0
        else return -1
    }

    override operator fun equals(other : Any?) : Boolean {
        if(other == null) return false
        if(this::class != other::class) return false
        val meter = other as Meter
        return (length == meter.length)
    }

    operator fun inc() : Meter {
        return Meter(length + 1)
    }

    operator fun dec() : Meter {
        return Meter(length - 1)
    }

    override fun toString(): String {
        return "$length meter"
    }

    fun a() {
        val a : Int
    }

    override fun hashCode(): Int {
        return length.hashCode()
    }
}


fun main() {
    var meter = Meter(5.0) + Meter(5.0)
    println("${Meter(5.0)} + ${Meter(5.0)} = $meter")

    meter = Meter(10.0) - Meter(2.0)
    println("${Meter(10.0)} - ${Meter(2.0)} = $meter")

    meter = Meter(10.0) * 2.0
    println("${Meter(10.0)} * 2.0 = $meter")

    meter = Meter(10.0) / 2.0
    println("${Meter(10.0)} / 2.0 = $meter")

    meter = Meter(10.0) % 2.0
    println("${Meter(10.0)} % 2.0 = $meter")

    val m = Meter(2.0)

    m += Meter(3.0)
    println("${Meter(2.0)} + ${Meter(3.0)} = $m")

    val n = Meter(4.0)
    n -= Meter(2.0)
    println("${Meter(4.0)} - ${Meter(2.0)} = $n")

    val o = Meter(3.0)
    o *= 2.0
    println("${Meter(3.0)} * 2.0 = $o")

    val p = Meter(6.0)
    p /= 2.0
    println("${Meter(6.0)} / 2.0 = $p")

    val r = Meter(6.0)
    p %= 2.0
    println("${Meter(6.0)} % 2.0 = $p")

    var result : Boolean
    result = Meter(10.0) >= Meter(2.0)
    println("${Meter(10.0)} >= ${Meter(2.0)} is $result")

    result = Meter(5.0) <= Meter(10.0)
    println("${Meter(5.0)} < ${Meter(10.0)} is $result")

    val value : Int
}

