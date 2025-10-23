package com.kuzNET.listvisualizerandroid.data

import com.kuzNET.listvisualizerandroid.data.DoubleType
import com.kuzNET.listvisualizerandroid.data.FractionType
import com.kuzNET.listvisualizerandroid.data.IntegerType
import com.kuzNET.listvisualizerandroid.data.StringType
import com.kuzNET.listvisualizerandroid.data.UserType
import kotlin.collections.ArrayList

class UserFactory {
    val allBuilders: List<UserType> = listOf(
        IntegerType(),
        DoubleType(),
        StringType(),
        FractionType()
    )

    val typeNameList: List<String>
        get() = allBuilders.map { it.typeName() }

    fun getBuilderByName(name: String?): UserType? {
        return allBuilders.find { it.typeName() == name }
    }
}
