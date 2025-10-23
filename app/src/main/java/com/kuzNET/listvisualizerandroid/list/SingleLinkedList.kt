package com.kuzNET.listvisualizerandroid.list

import com.kuzNET.listvisualizerandroid.data.Comparator
import com.kuzNET.listvisualizerandroid.data.UserType
import java.io.*
import org.json.JSONArray
import org.json.JSONObject

class SingleLinkedList(val userType: UserType) {

    private class Node(var value: Any?, var next: Node? = null)

    private var head: Node? = null
    var size = 0
        private set

    fun add(obj: Any?) {
        val newNode = Node(obj)
        if (head == null) {
            head = newNode
        } else {
            var current = head
            while (current?.next != null) {
                current = current.next
            }
            current?.next = newNode
        }
        size++
    }

    // Добавление в начало
    fun addFirst(obj: Any?) {
        head = Node(obj, head)
        size++
    }

    // Вставка по индексу
    fun insert(index: Int, obj: Any?) {
        if (index < 0 || index > size) throw IndexOutOfBoundsException()
        if (index == 0) {
            addFirst(obj)
            return
        }
        var current = head
        for (i in 0 until index - 1) {
            current = current?.next
        }
        current?.next = Node(obj, current?.next)
        size++
    }


    fun get(index: Int): Any? {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException()
        var current = head
        for (i in 0 until index) {
            current = current?.next
        }
        return current?.value
    }

    // Удаление по индексу
    fun remove(index: Int): Any? {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException()
        val removedValue: Any?
        if (index == 0) {
            removedValue = head?.value
            head = head?.next
        } else {
            var current = head
            for (i in 0 until index - 1) {
                current = current?.next
            }
            removedValue = current?.next?.value
            current?.next = current?.next?.next
        }
        size--
        return removedValue
    }

    // Преобразование в обычный список для отображения
    fun toList(): List<Any?> {
        val list = mutableListOf<Any?>()
        var current = head
        while (current != null) {
            list.add(current.value)
            current = current.next
        }
        return list
    }

    fun sort() {
        if (size <= 1) return
        val array = toList().toTypedArray()
        quickSort(array, 0, size - 1, userType.typeComparator)
        head = null
        size = 0
        array.forEach { add(it) }
    }

    private fun quickSort(a: Array<Any?>, lo: Int, hi: Int, comp: Comparator) {
        if (lo >= hi) return
        val p = partition(a, lo, hi, comp)
        quickSort(a, lo, p - 1, comp)
        quickSort(a, p + 1, hi, comp)
    }

    private fun partition(a: Array<Any?>, lo: Int, hi: Int, comp: Comparator): Int {
        val pivot = a[hi]
        var i = lo - 1
        for (j in lo until hi) {
            if (comp.compare(a[j], pivot) <= 0) {
                i++
                val tmp = a[i]
                a[i] = a[j]
                a[j] = tmp
            }
        }
        val tmp = a[i + 1]
        a[i + 1] = a[hi]
        a[hi] = tmp
        return i + 1
    }

    // Сохранение и загрузка
    fun saveToJson(outputStream: OutputStream) {
        val json = JSONObject()
        json.put("type", userType.typeName())
        val items = JSONArray()
        toList().forEach { item ->
            items.put(userType.serialize(item))
        }
        // Записываем в предоставленный поток данных
        outputStream.bufferedWriter().use { writer ->
            writer.write(json.toString(4))
        }
    }

    fun loadFromJson(inputStream: InputStream) {
        // Читаем из предоставленного потока данных
        val jsonText = inputStream.bufferedReader().use { it.readText() }
        val json = JSONObject(jsonText)
        val items = json.getJSONArray("items")
        head = null
        size = 0
        for (i in 0 until items.length()) {
            val s = items.getString(i)
            add(userType.deserialize(s))
        }
    }

    fun saveToBinary(outputStream: OutputStream) {
        DataOutputStream(BufferedOutputStream(outputStream)).use { dos ->
            dos.writeUTF(userType.typeName())
            dos.writeInt(size)
            toList().forEach { item ->
                dos.writeUTF(userType.serialize(item))
            }
        }
    }

    fun loadFromBinary(inputStream: InputStream) {
        DataInputStream(BufferedInputStream(inputStream)).use { dis ->
            // Тип мы уже прочитаем снаружи, так что здесь его пропускаем
            dis.readUTF()
            val count = dis.readInt()
            head = null
            size = 0
            for (i in 0 until count) {
                val s = dis.readUTF()
                add(userType.deserialize(s))
            }
        }
    }

    companion object {
        // Эти "разведчики" теперь тоже работают с потоками
        fun readTypeNameFromJson(inputStream: InputStream): String? {
            return try {
                val jsonText = inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(jsonText)
                json.getString("type")
            } catch (e: Exception) {
                null
            }
        }

        fun readTypeNameFromBinary(inputStream: InputStream): String? {
            return try {
                DataInputStream(BufferedInputStream(inputStream)).use { dis ->
                    dis.readUTF()
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}