package com.openclassrooms.realestatemanager.property_main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.model_ui.EmptyProperty
import com.openclassrooms.realestatemanager.property_edit.EditActivity
import com.openclassrooms.realestatemanager.property_list.ListFragment
import com.openclassrooms.realestatemanager.repository.InMemoryRepository

class MainActivity : AppCompatActivity() {

    private val inMemoRepo = InMemoryRepository.getInstance()

    private var listFragment: ListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //SEARCH BAR
        val searchEditText: TextView = findViewById(R.id.et_search_bar)
        searchEditText.text = "" //TODO: Emptying last search
        searchEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { passSearchToListFragment(it) }
            }
        })
    }

    private fun passSearchToListFragment(text: CharSequence) {

        if (listFragment == null) {
            listFragment = supportFragmentManager.findFragmentById(R.id.fragment_list_main) as ListFragment
        }
        listFragment?.filterByDescription(text)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        //menu?.clear()
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.item_main_menu_add -> {
                inMemoRepo.setPropertySelection(EmptyProperty)
                startActivity(EditActivity.newIntent(this))
                true
            }
            R.id.item_main_menu_search -> {
                //Display Search bar
                findViewById<View>(R.id.layout_search_bar_main).visibility = View.VISIBLE
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val LIST_FRAG_TAG = "list_fragment"
        const val DETAILS_FRAG_TAG = "details_fragment"
    }
}