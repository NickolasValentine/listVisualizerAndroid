package com.kuzNET.listvisualizerandroid.data

import kotlin.math.abs

data class Fraction(var whole: Long = 0, var num: Long = 0, var den: Long = 1) {
    init {
        normalize()
    }

    private fun normalize() {
        if (den == 0L) den = 1
        if (den < 0) {
            den = -den
            num = -num
        }
        if (num < 0 && whole > 0) {
            whole -= 1
            num = den - -num % den
        } else if (num >= den) {
            val add = num / den
            whole += add
            num %= den
        }
        if (whole < 0 && num > 0) num = -num
        val g = gcd(abs(num), abs(den))
        if (g != 0L) {
            num /= g
            den /= g
        }
    }

    private fun gcd(a: Long, b: Long): Long {
        var aVar = a
        var bVar = b
        while (bVar != 0L) {
            val t = bVar
            bVar = aVar % bVar
            aVar = t
        }
        return abs(aVar)
    }

    fun toDouble(): Double {
        return whole.toDouble() + num.toDouble() / den.toDouble()
    }

    override fun toString(): String {
        if (num == 0L) return whole.toString()
        if (whole == 0L) return "$num/$den"
        return "$whole ${abs(num)}/$den"
    }
}
