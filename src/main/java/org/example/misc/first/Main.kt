package org.example.misc.first

interface NoNameInterface

fun f(request : Boolean) : NoNameInterface? {
    if(request) return Int::class as NoNameInterface else return null
}

class Foo(val classifier : NoNameInterface?) {

}

fun main() {
    val foo = Foo(null)

    if(foo.classifier == null) {
        println("It's not class and not TypeParameter")
    }
}


/*
fun main() {
    val result = f(false)

    if(result == null) return
}
 */