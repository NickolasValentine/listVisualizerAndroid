package com.kuzNET.listvisualizerandroid

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kuzNET.listvisualizerandroid.data.UserFactory
import com.kuzNET.listvisualizerandroid.data.UserType
import com.kuzNET.listvisualizerandroid.list.SingleLinkedList
import java.io.File
import android.content.ContentResolver
import android.net.Uri

class ListViewModel : ViewModel() {

    private val userFactory = UserFactory()

    // Текущий выбранный тип данных
    var currentUserType: UserType = userFactory.getBuilderByName("Integer")!!
        private set

    // Наш односвязный список
    private var linkedList = SingleLinkedList(currentUserType)

    // LiveData - это специальный класс, который уведомляет интерфейс об изменениях
    // Когда мы изменим _listData, интерфейс автоматически обновится
    private val _listData = MutableLiveData<List<Any?>>()
    val listData: LiveData<List<Any?>> get() = _listData

    init {
        // При создании ViewModel сразу публикуем пустой список
        updateLiveData()
    }

    // Метод для обновления LiveData
    private fun updateLiveData() {
        _listData.value = linkedList.toList()
    }

    // --- Операции со списком ---

    fun changeType(typeName: String) {
        // ЭТА ПРОВЕРКА ЧТОБЫ НЕ ПЕРЕДЕЛЫВАЛСЯ ПРИ ПОВОРОТЕ
        // Если тип не изменился, ничего не делаем и выходим.
        if (currentUserType.typeName() == typeName) {
            return
        }

        // Этот код выполнится, только если пользователь выбрал ДРУГОЙ тип
        currentUserType = userFactory.getBuilderByName(typeName)!!
        linkedList = SingleLinkedList(currentUserType) // Создаем новый пустой список
        updateLiveData()
    }

    fun add(item: Any?, index: Int? = null) {
        when {
            index == null -> linkedList.add(item) // В конец
            index == 0 -> linkedList.addFirst(item) // В начало
            else -> linkedList.insert(index, item)
        }
        updateLiveData()
    }

    fun remove(index: Int) {
        if (index >= 0 && index < linkedList.size) {
            linkedList.remove(index)
            updateLiveData()
        }
    }

    fun sort() {
        linkedList.sort()
        updateLiveData()
    }

    fun find(valueStr: String): Int {
        val valueToFind = currentUserType.parseValue(valueStr)
        return linkedList.toList().indexOf(valueToFind)
    }

    // --- Сохранение и загрузка ---

    fun save(contentResolver: ContentResolver, uri: Uri) {
        contentResolver.openOutputStream(uri)?.use { outputStream ->
            // Определяем формат по URI, если возможно (упрощенно)
            if (uri.toString().endsWith(".json")) {
                linkedList.saveToJson(outputStream)
            } else {
                linkedList.saveToBinary(outputStream)
            }
        }
    }

    fun loadFromFile(contentResolver: ContentResolver, uri: Uri): String? {
        val extension = if (uri.toString().endsWith(".json")) "json" else "bin"

        // Сначала "заглядываем" в файл, чтобы узнать тип
        val typeName = contentResolver.openInputStream(uri)?.use { inputStream ->
            if (extension == "json") {
                SingleLinkedList.readTypeNameFromJson(inputStream)
            } else {
                SingleLinkedList.readTypeNameFromBinary(inputStream)
            }
        }

        if (typeName != null) {
            changeType(typeName)
            // Открываем поток заново для полной загрузки
            contentResolver.openInputStream(uri)?.use { inputStream ->
                if (extension == "json") {
                    linkedList.loadFromJson(inputStream)
                } else {
                    linkedList.loadFromBinary(inputStream)
                }
            }
            updateLiveData()
            return typeName
        }
        return null
    }
}