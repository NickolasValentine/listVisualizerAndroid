package com.kuzNET.listvisualizerandroid.data

// Компаратор для сравнения двух объектов
interface Comparator {
    fun compare(o1: Any?, o2: Any?): Int
}