package com.kuzNET.listvisualizerandroid


import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.GravityCompat
import com.kuzNET.listvisualizerandroid.data.UserFactory
import com.kuzNET.listvisualizerandroid.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ListViewModel by viewModels()
    private lateinit var listAdapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.root.findViewById(R.id.toolbar))
        supportActionBar?.title = ""

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.root.findViewById(R.id.toolbar),
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        toggle.drawerArrowDrawable.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            Color.BLACK, BlendModeCompat.SRC_ATOP)

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        setupDrawerMenu()

        setupSpinner()
        setupRecyclerView()

        viewModel.listData.observe(this) { items ->
            listAdapter.updateData(items)
        }
    }

    // --- Лаунчер для сохранения файла ---
    // Он вызывает системный экран "сохранить как..."
    private val saveFileLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        // Этот код выполнится, когда пользователь выберет место и нажмет "Сохранить"
        uri?.let {
            viewModel.save(contentResolver, it)
            Toast.makeText(this, "File saved successfully", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Лаунчер для открытия файла ---
    // Он вызывает системный экран выбора файла
    private val openFileLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        // Этот код выполнится, когда пользователь выберет файл
        uri?.let {
            val loadedTypeName = viewModel.loadFromFile(contentResolver, it)
            if (loadedTypeName != null) {
                // Обновляем Spinner
                val spinner = binding.root.findViewById<android.widget.Spinner>(R.id.spinnerType)
                val adapter = spinner.adapter as ArrayAdapter<String>
                val position = adapter.getPosition(loadedTypeName)
                if (position >= 0) {
                    spinner.setSelection(position)
                }
                Toast.makeText(this, "Loaded successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to read file format", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Создание меню в Toolbar (Save/Load)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    // Обработка нажатий на кнопки в Toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> showSaveDialog()
            R.id.action_load -> showLoadDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        listAdapter = ListAdapter(emptyList())
        binding.root.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView).adapter = listAdapter
    }

    private fun setupSpinner() {
        val spinner = binding.root.findViewById<android.widget.Spinner>(R.id.spinnerType)
        val typeNames = UserFactory().typeNameList
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Устанавливаем в спиннере тот тип, который сейчас активен в ViewModel
        // Это синхронизирует UI с данными после поворота экрана
        val currentPosition = typeNames.indexOf(viewModel.currentUserType.typeName())
        if (currentPosition != -1) {
            spinner.setSelection(currentPosition)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Теперь этот вызов безопасен благодаря проверке внутри ViewModel
                viewModel.changeType(typeNames[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupDrawerMenu() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_add -> showAddOptionsDialog()
                R.id.nav_delete -> showDeleteOptionsDialog()
                R.id.nav_search -> showSearchDialog()
                R.id.nav_sort -> {
                    viewModel.sort()
                    Toast.makeText(this, "List sorted", Toast.LENGTH_SHORT).show()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    // --- Диалоговые окна ---

    private fun showAddOptionsDialog() {
        val options = arrayOf("Add to start", "Add to end", "Add by index")
        AlertDialog.Builder(this)
            .setTitle("Add element")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showAddDialog(0) // В начало
                    1 -> showAddDialog(null) // В конец
                    2 -> showGetIndexDialog("Enter index to add") { index ->
                        showAddDialog(index)
                    }
                }
            }
            .show()
    }

    private fun showDeleteOptionsDialog() {
        val options = arrayOf("Delete from start", "Delete from end", "Delete by index")
        AlertDialog.Builder(this)
            .setTitle("Delete element")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> viewModel.remove(0)
                    1 -> viewModel.remove(viewModel.listData.value?.size?.minus(1) ?: -1)
                    2 -> showGetIndexDialog("Enter index to delete") { index ->
                        viewModel.remove(index)
                    }
                }
            }
            .show()
    }

    private fun showAddDialog(index: Int?) {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Enter value")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val text = editText.text.toString()
                try {
                    val value = viewModel.currentUserType.parseValue(text)
                    viewModel.add(value, index)
                } catch (e: Exception) {
                    Toast.makeText(this, "Invalid format", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showGetIndexDialog(title: String, onResult: (Int) -> Unit) {
        val editText = EditText(this)
        editText.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                val index = editText.text.toString().toIntOrNull()
                if (index != null) {
                    onResult(index)
                } else {
                    Toast.makeText(this, "Invalid index", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSearchDialog() {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Enter value to find")
            .setView(editText)
            .setPositiveButton("Find") { _, _ ->
                val index = viewModel.find(editText.text.toString())
                if (index != -1) {
                    Toast.makeText(this, "Element found at index: $index", Toast.LENGTH_LONG).show()
                    binding.root.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView).scrollToPosition(index)
                } else {
                    Toast.makeText(this, "Element not found", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSaveDialog() {
        val options = arrayOf("Save to .json", "Save to .bin")
        AlertDialog.Builder(this)
            .setTitle("Save list as...")
            .setItems(options) { _, which ->
                val fileName = "my_list.${if (which == 0) "json" else "bin"}"
                // Запускаем системный диалог сохранения
                saveFileLauncher.launch(fileName)
            }
            .show()
    }

    private fun showLoadDialog() {
        // Запускаем системный диалог открытия
        openFileLauncher.launch(arrayOf("*/*")) // Показываем все типы файлов
    }
}