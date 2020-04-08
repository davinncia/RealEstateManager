package com.openclassrooms.realestatemanager.view.edit

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.di.ViewModelFactory
import com.openclassrooms.realestatemanager.utils.bind
import com.openclassrooms.realestatemanager.view.map.MapsActivity
import com.openclassrooms.realestatemanager.view.model_ui.AddressUi
import com.openclassrooms.realestatemanager.view.model_ui.PoiUi
import com.openclassrooms.realestatemanager.view.model_ui.PropertyUi
import com.openclassrooms.realestatemanager.view.search.PoiAdapter

class EditActivity : AppCompatActivity(), PictureEditAdapter.DeleteButtonClickListener, PoiAdapter.OnPoiCLickListener {


    //DATA
    private lateinit var viewModel: EditViewModel

    // TODO LUCAS A "stocker" côté ViewModel
    private var isNew: Boolean = true
    private var id = -1

    //VIEW
    private val spinner by bind<Spinner>(R.id.spinner_edit_type)
    private val priceView by bind<EditText>(R.id.et_edit_price)
    private val areaView by bind<EditText>(R.id.et_edit_area)
    private val roomsView by bind<EditText>(R.id.et_edit_room_nbr)
    private val descriptionView by bind<EditText>(R.id.et_edit_description)
    private val cityView by bind<EditText>(R.id.et_edit_city)
    private val streetView by bind<EditText>(R.id.et_edit_street)
    private val streetNbrView by bind<EditText>(R.id.et_edit_street_nbr)
    private val agentView by bind<EditText>(R.id.et_edit_agent_name)
    private val addImageView by bind<ImageView>(R.id.iv_edit_add_picture)

    // TODO LUCAS Utilise le minimum de property, tu peux utiliser uniquement des variables locales
    //  pour les adapters (tu les passes en paramètres de function)
    private lateinit var pictureAdapter: PictureEditAdapter
    private lateinit var poiAdapter: PoiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.property_infos)

        addImageView.setOnClickListener { checkStoragePermission() }

        initPoiRecyclerView()
        initPictureRecyclerView()

        viewModel = ViewModelProvider(this, ViewModelFactory(this.application)).get(EditViewModel::class.java)


        viewModel.selectedProperty.observe(this, Observer {
            //Editing an already existing property
            isNew = false
            id = it.id
            completeEditTexts(it)
        })

        //Display pics
        viewModel.allPictures.observe(this, Observer {
            pictureAdapter.populateData(it)
        })

        //Display poi
        viewModel.allPoi.observe(this, Observer {
            poiAdapter.populateData(it)
        })

    }

    //--------------------------------------------------------------------------------------//
    //                                         U I
    //--------------------------------------------------------------------------------------//

    private fun completeEditTexts(property: PropertyUi) {
        priceView.setText(property.price.toString())
        areaView.setText(property.area.toString())
        roomsView.setText(property.roomNbr.toString())
        descriptionView.setText(property.description)
        cityView.setText(property.address.city)
        streetView.setText(property.address.street)
        streetNbrView.setText(property.address.streetNbr.toString())
        agentView.setText(property.agentName)
    }

    //RecyclerViews
    private fun initPoiRecyclerView() {

        poiAdapter = PoiAdapter(this)

        findViewById<RecyclerView>(R.id.recycler_view_poi_edit).apply {
            adapter = poiAdapter
            layoutManager = LinearLayoutManager(
                    this@EditActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onPoiClicked(poi: PoiUi) {
        viewModel.handlePoiSelection(poi)
    }

    private fun initPictureRecyclerView() {

        pictureAdapter = PictureEditAdapter(this)

        findViewById<RecyclerView>(R.id.recycler_view_edit).apply {
            adapter = pictureAdapter
            layoutManager = LinearLayoutManager(
                    this@EditActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun checkInputsAndSave() {
        //TYPE
        val type = spinner.selectedItem.toString()
        //PRICE
        val strPrice = priceView.text.toString()
        //AREA
        val strArea = areaView.text.toString()
        //ROOM NUMBER
        val strRooms = roomsView.text.toString()
        //DESCRIPTION
        val description = descriptionView.text.toString()
        //CITY
        val city = cityView.text.toString()
        //STREET
        val street = streetView.text.toString()
        //STREET NBR
        val strStreetNbr = streetNbrView.text.toString()
        //AGENT
        val agent = agentView.text.toString()

        val inputs = arrayOf(type, strPrice, strArea, strRooms, description, city, street, strStreetNbr, agent)

        // TODO LUCAS Tu peux utiliser isBlank() pour éviter les petits rigolos qui mettent des espaces :p
        if (inputs.any { it.isEmpty() }) {
            Toast.makeText(this, getString(R.string.field_missing), Toast.LENGTH_SHORT).show()
        } else {

            // TODO LUCAS A faire côté ViewModel (balance juste des Strings à ton ViewModel c'est son rôle de parser)
            val address = AddressUi(city, street, strStreetNbr.toInt())

            // TODO LUCAS A faire côté ViewModel (balance juste des Strings à ton ViewModel c'est son rôle de parser)
            val property = PropertyUi(type, strPrice.toInt(), strArea.toFloat(), strRooms.toInt(),
                    description, address, agent, false, id)

            viewModel.saveInDb(property, isNew)
            Toast.makeText(this, getString(R.string.saving), Toast.LENGTH_SHORT).show()
            this.finish()
        }

    }

    //--------------------------------------------------------------------------------------//
    //                                    P I C T U R E S
    //--------------------------------------------------------------------------------------//
    private fun launchImageIntents() {

        val chooserIntent = Intent(Intent.ACTION_CHOOSER)

        //GALLERY INTENT
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        chooserIntent.putExtra(Intent.EXTRA_INTENT, galleryIntent)

        //CAMERA INTENT
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent))
        }

        if (chooserIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(chooserIntent, REQUEST_IMAGE_CODE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CODE && resultCode == RESULT_OK) {
            // TODO LUCAS A faire côté ViewModel (balance juste le data au ViewModel, il se débrouille)
            //  viewModel.addPicture(data)
            val uri: String? =
                    if (data?.data != null) {
                        //GALLERY
                        data.data.toString()

                    } else {
                        //CAMERA
                        val imageBitmap = data?.extras?.get("data") as Bitmap
                        viewModel.saveImageInMediaStore(imageBitmap)?.toString()
                    }

            //Pass it to the viewModel
            uri?.let { viewModel.addPicture(it) }
        }
    }

    override fun onDeleteButtonClick(pictureUri: String, position: Int) {
        viewModel.deletePictureFromDb(pictureUri, position)
    }

    //--------------------------------------------------------------------------------------//
    //                                      M E N U
    //--------------------------------------------------------------------------------------//
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.item_edit_menu_save -> {
                checkInputsAndSave()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //--------------------------------------------------------------------------------------//
    //                                   P E R M I S S I O N S
    //--------------------------------------------------------------------------------------//
    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //Not granted yet
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MapsActivity.LOCATION_REQUEST_CODE)
        } else {
            //OK
            launchImageIntents()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MapsActivity.LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //Not granted
                } else {
                    launchImageIntents()
                }
            }
        }
    }

    //--------------------------------------------------------------------------------------//
    //                                   C O M P A N I O N
    //--------------------------------------------------------------------------------------//
    companion object {
        private const val REQUEST_IMAGE_CODE = 1

        fun newIntent(context: Context) = Intent(context, EditActivity::class.java)
    }

}
