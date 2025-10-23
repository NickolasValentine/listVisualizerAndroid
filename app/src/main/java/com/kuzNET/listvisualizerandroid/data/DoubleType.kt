package com.kuzNET.listvisualizerandroid.data

import java.io.*
import com.kuzNET.listvisualizerandroid.data.Comparator

class DoubleType : UserType {
    override fun typeName(): String = "Double"
    override fun create(): Any = 0.0
    override fun cloneObject(obj: Any?): Any = obj as? Double ?: create()
    override fun parseValue(ss: String?): Any = ss?.trim()?.toDoubleOrNull() ?: 0.0

    override val typeComparator: Comparator = object : Comparator {
        override fun compare(o1: Any?, o2: Any?): Int {
            return (o1 as Double).compareTo(o2 as Double)
        }
    }
    override fun serialize(obj: Any?): String = obj?.toString() ?: ""
    override fun deserialize(s: String?): Any? = parseValue(s)
}