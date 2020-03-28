package com.openclassrooms.realestatemanager.view.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.model.Criteria
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.view.edit.EditActivity
import com.openclassrooms.realestatemanager.view.list.ListFragment
import com.openclassrooms.realestatemanager.view.model_ui.EmptyProperty
import com.openclassrooms.realestatemanager.view.search.SearchActivity

class MainActivity : AppCompatActivity() {

    private val inMemoRepo = InMemoryRepository.getInstance()

    private var listFragment: ListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listFragment = supportFragmentManager.findFragmentById(R.id.fragment_list_main) as ListFragment

        //SEARCH BAR
        val searchEditText: TextView = findViewById(R.id.et_search_bar)
        searchEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { passSearchToListFragment(it) }
            }
        })

        findViewById<ImageView>(R.id.iv_advanced_search).setOnClickListener {
            startActivityForResult(SearchActivity.newIntent(this), SearchActivity.CRITERIA_RC)
        }
    }

    //-------------------------------------------------------------------------------------------//
    //                                        S E A R C H
    //-------------------------------------------------------------------------------------------//
    private fun passSearchToListFragment(text: CharSequence) {
        listFragment?.filterByDescription(text)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SearchActivity.CRITERIA_RC && resultCode == Activity.RESULT_OK) {
            //Criteria returning
            val criteria = data?.getParcelableExtra<Criteria>(SearchActivity.CRITERIA_EXTRA)
            criteria?.let { listFragment?.passSearchCriteria(it) }

        }
    }

    //-------------------------------------------------------------------------------------------//
    //                                         M E N U
    //-------------------------------------------------------------------------------------------//
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