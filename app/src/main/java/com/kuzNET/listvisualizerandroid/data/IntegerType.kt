package com.kuzNET.listvisualizerandroid.data

import java.io.*

class IntegerType : UserType {
    override fun typeName(): String = "Integer"
    override fun create(): Any = 0
    override fun cloneObject(obj: Any?): Any = obj as? Int ?: create()
    override fun parseValue(ss: String?): Any = ss?.trim()?.toIntOrNull() ?: 0

    override val typeComparator: Comparator = object : Comparator {
        override fun compare(o1: Any?, o2: Any?): Int {
            return (o1 as Int).compareTo(o2 as Int)
        }
    }

    override fun serialize(obj: Any?): String = obj?.toString() ?: ""
    override fun deserialize(s: String?): Any? = parseValue(s)
}
