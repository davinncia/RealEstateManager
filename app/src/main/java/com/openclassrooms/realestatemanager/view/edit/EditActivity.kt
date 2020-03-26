package com.openclassrooms.realestatemanager.view.edit

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.di.ViewModelFactory
import com.openclassrooms.realestatemanager.view.map.MapsActivity
import com.openclassrooms.realestatemanager.view.model_ui.AddressUi
import com.openclassrooms.realestatemanager.view.model_ui.PoiUi
import com.openclassrooms.realestatemanager.view.model_ui.PropertyUi
import com.openclassrooms.realestatemanager.view.search.PoiAdapter
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity(), PictureEditAdapter.DeleteButtonClickListener, PoiAdapter.OnPoiCLickListener {

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
    private lateinit var addImageView: ImageView

    private lateinit var pictureAdapter: PictureEditAdapter
    private lateinit var poiAdapter: PoiAdapter

    private var cameraPhotoPath: String? = null

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
    private fun  initPoiRecyclerView() {

        poiAdapter = PoiAdapter(this)

        findViewById<RecyclerView>(R.id.recycler_view_poi_edit).apply {
            adapter = poiAdapter
            layoutManager = LinearLayoutManager(this@EditActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onPoiClicked(poi: PoiUi) {
        viewModel.handlePoiSelection(poi)
    }

    private fun initPictureRecyclerView() {

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

        //TAKE PICTURE
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile (this,
                "com.openclassrooms.realestatemanager.fileprovider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                // Save a file: path for use with ACTION_VIEW intents
                cameraPhotoPath = photoFile.absolutePath
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent))
            }
        }

        //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent))

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

            } else {
                //CAMERA
                //val imageBitmap = data?.extras?.get("data") as Bitmap
                //val bytes = ByteArrayOutputStream()
                //imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                //uri = MediaStore.Images.Media.insertImage(contentResolver, imageBitmap, "Title", null)

                //uri = saveImageInMediaStore(imageBitmap)?.toString()

                uri = cameraPhotoPath!!
            }

            //Pass it to the viewModel
            //uri?.let { viewModel.addPicture(it) }
            viewModel.addPicture(uri)
        }

    }

    private fun saveImageInMediaStore(pic: Bitmap): Uri? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, timeStamp)
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //    put(MediaStore.Images.Media.RELATIVE_PATH, "RealEstate")
            //}
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = applicationContext.contentResolver
        //ALWAYS NULL...
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        uri?.let {
            resolver.openOutputStream(uri)?.use { outputStream ->
                pic.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            }

            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }
        return uri
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        )
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
