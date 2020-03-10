package com.openclassrooms.realestatemanager.property_edit

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
import com.openclassrooms.realestatemanager.model_ui.AddressUi
import com.openclassrooms.realestatemanager.model_ui.PropertyUi
import com.openclassrooms.realestatemanager.property_map.MapsActivity
import java.io.ByteArrayOutputStream

class EditActivity : AppCompatActivity(), PictureEditAdapter.DeleteButtonClickListener {

    private lateinit var viewModel: EditViewModel

    private var isNew: Boolean = true
    private var id = -1

    private lateinit var spinner: Spinner
    private lateinit var priceView: EditText
    private lateinit var areaView: EditText
    private lateinit var roomsView: EditText
    private lateinit var descriptionView: EditText
    private lateinit var cityView: EditText
    private lateinit var streetView: EditText
    private lateinit var streetNbrView: EditText
    private lateinit var agentView: EditText
    private lateinit var addImageView: ImageView //TODO: DEBUG

    private lateinit var pictureAdapter: PictureEditAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        spinner = findViewById(R.id.spinner_edit_type)
        priceView = findViewById(R.id.et_edit_price)
        areaView = findViewById(R.id.et_edit_area)
        roomsView = findViewById(R.id.et_edit_room_nbr)
        descriptionView = findViewById(R.id.et_edit_description)
        cityView = findViewById(R.id.et_edit_city)
        streetView = findViewById(R.id.et_edit_street)
        streetNbrView = findViewById(R.id.et_edit_street_nbr)
        agentView = findViewById(R.id.et_edit_agent_name)
        addImageView = findViewById<ImageView>(R.id.iv_edit_add_picture).apply { setOnClickListener { checkStoragePermission() } }

        initRecyclerView()

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

    //RecyclerView
    private fun initRecyclerView() {

        pictureAdapter = PictureEditAdapter(this)

        findViewById<RecyclerView>(R.id.recycler_view_edit).apply {
            adapter = pictureAdapter
            layoutManager = LinearLayoutManager(this@EditActivity, LinearLayoutManager.HORIZONTAL, false)
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

        if (inputs.any { it.isEmpty() }) {
            Toast.makeText(this, getString(R.string.field_missing), Toast.LENGTH_SHORT).show()
        } else {

            val address = AddressUi(city, street, strStreetNbr.toInt())

            val property = PropertyUi(type, strPrice.toFloat(), strArea.toFloat(), strRooms.toInt(),
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
        chooserIntent.putExtra(Intent.EXTRA_INTENT, galleryIntent)
        //TAKE PICTURE
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent))

        if (chooserIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(chooserIntent, REQUEST_IMAGE_CODE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CODE && resultCode == RESULT_OK) {
            val uri: String

            if (data?.data != null) {
                //GALLERY
                uri = data.data.toString()
                //TODO NINO: already present in MediaStore, save anyway ?

            } else {
                //CAMERA
                val imageBitmap = data?.extras?.get("data") as Bitmap
                val bytes = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                uri = MediaStore.Images.Media.insertImage(contentResolver, imageBitmap, "Title", null)//TODO: deprecated
                //TODO NINO: deprecated
            }

            //Pass it to the viewModel
            viewModel.addPicture(uri)
        }

    }

    private fun deletePicture(uri: String, position: Int) {
        //DB
        viewModel.deletePictureFromDb(uri, position)
    }
    //TODO: Nino: remove from media store ?
    /*
    // Remove a specific media item.
    val resolver = applicationContext.contentResolver

    // URI of the image to remove.
    val imageUri = "..."

    // WHERE clause.
    val selection = "..."
    val selectionArgs = "..."

    // Perform the actual removal.
    val numImagesRemoved = resolver.delete(
            imageUri,
            selection,
            selectionArgs)

     */

    /* PRIVATE STORAGE
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            //currentPhotoPath = absolutePath
        }
    }
     */


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
        private const val IS_NEW_KEY = "is_new_key"
        private const val REQUEST_IMAGE_CODE = 1

        fun newIntent(context: Context) = Intent(context, EditActivity::class.java)
        /*
        fun newIntent(context: Context, isNew: Boolean): Intent {
            val intent = Intent(context, EditActivity::class.java)
            intent.putExtra(IS_NEW_KEY, isNew)
            return intent
        }

         */
    }

    override fun onDeleteButtonClick(pictureUri: String, position: Int) {
        deletePicture(pictureUri, position)
    }
}
