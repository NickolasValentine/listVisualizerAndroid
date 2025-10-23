package com.kuzNET.listvisualizerandroid.data

import java.io.*

class StringType : UserType {
    override fun typeName(): String = "String"
    override fun create(): Any = ""
    override fun cloneObject(obj: Any?): Any = obj as? String ?: create()
    override fun parseValue(ss: String?): Any = ss ?: ""

    override val typeComparator: Comparator = object : Comparator {
        override fun compare(o1: Any?, o2: Any?): Int {
            return (o1 as String).compareTo(o2 as String)
        }
    }

    override fun serialize(obj: Any?): String = obj?.toString() ?: ""
    override fun deserialize(s: String?): Any? = s
}