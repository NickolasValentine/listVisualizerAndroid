package com.kuzNET.listvisualizerandroid.data

import com.kuzNET.listvisualizerandroid.data.Comparator
import java.io.*
import java.io.InputStreamReader

interface UserType {
    fun typeName(): String
    fun create(): Any
    fun cloneObject(obj: Any?): Any
    fun parseValue(ss: String?): Any
    val typeComparator: Comparator
    fun serialize(obj: Any?): String
    fun deserialize(s: String?): Any?
}
