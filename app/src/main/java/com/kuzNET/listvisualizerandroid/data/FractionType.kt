package com.kuzNET.listvisualizerandroid.data

import java.io.*

class FractionType : UserType {
    override fun typeName(): String = "Fraction"
    override fun create(): Any = Fraction()
    override fun cloneObject(obj: Any?): Any {
        val f = obj as? Fraction ?: return create()
        return f.copy()
    }

    override fun parseValue(ss: String?): Any {
        if (ss.isNullOrBlank()) return Fraction()
        val s = ss.trim()
        try {
            if (s.contains(" ")) {
                val parts = s.split("\\s+".toRegex())
                val w = parts[0].toLong()
                val nd = parts[1].split("/")
                val n = nd[0].toLong()
                val d = nd[1].toLong()
                return Fraction(w, n, d)
            }
            if (s.contains("/")) {
                val nd = s.split("/")
                val n = nd[0].toLong()
                val d = nd[1].toLong()
                return Fraction(0, n, d)
            }
            return Fraction(s.toLong(), 0, 1)
        } catch (e: Exception) {
            return Fraction()
        }
    }

    override val typeComparator: Comparator = object : Comparator {
        override fun compare(o1: Any?, o2: Any?): Int {
            val a = o1 as Fraction
            val b = o2 as Fraction
            return a.toDouble().compareTo(b.toDouble())
        }
    }

    override fun serialize(obj: Any?): String = obj?.toString() ?: ""
    override fun deserialize(s: String?): Any? = parseValue(s)
}
