package com.mil.missingartifact.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.mil.missingartifact.R
import com.mil.missingartifact.viewModel.ArtifactViewModel
import java.io.*
import java.util.*


private const val TAG = "ArtifactMainActivity"

class MainActivity : AppCompatActivity() {

    // Storage Permissions
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private lateinit var vm: ArtifactViewModel

    //UI
    private val btnReadFile by lazy { findViewById<Button>(R.id.btnReadFile) }
    private val btnReset by lazy { findViewById<Button>(R.id.btnReset) }
    private val btnSelectFromDevice by lazy { findViewById<Button>(R.id.btnSelectFromDevice) }
    private val tvResult by lazy { findViewById<TextView>(R.id.tvResult) }
    private val tvCurrentFile by lazy { findViewById<TextView>(R.id.tvCurrentFile) }
    private val tvListSum by lazy { findViewById<TextView>(R.id.tvSum) }

    //Current File
    private var currentFile = "input.txt"

    //File Uri
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            hide()
        }

        //View Model
        vm = ViewModelProvider(this)[ArtifactViewModel::class.java]

        //Read File Button
        btnReadFile.setOnClickListener {
            displayResultText(vm.readFile(currentFile, application))
            writeToFile(
                vm.readFile(currentFile, application),
                "output-${Calendar.getInstance().timeInMillis}.txt"
            )
        }

        //Reset Button
        btnReset.setOnClickListener {
            reset()
        }

        //Selects File from device
        btnSelectFromDevice.setOnClickListener {
            selectFileFromDevice()
        }
    }

    private fun reset() {
        tvResult.text = getString(R.string.the_resulting_text_is_displayed_here)
        tvCurrentFile.text = getString(R.string.current_file_input_txt)
        tvListSum.text = getString(R.string.sum, "0")
        currentFile = "input.txt"
        vm.listSum = 0
    }

    private fun displayResultText(list: MutableList<String>) {
        tvResult.text = list.toString()
        tvCurrentFile.text = "Current File : $currentFile"
        tvListSum.text = getString(R.string.sum, vm.listSum.toString())
        vm.listSum = 0
    }

    private fun selectFileFromDevice() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        reset()
        uri = data!!.data!!
        val fileName = File(uri!!.path.toString()).name
        currentFile = fileName
        val fileContent = readTextFile(uri)
        displayResultText(fileContent)
    }

    private fun readTextFile(uri: Uri?): MutableList<String> {
        var reader: BufferedReader? = null
        val resultList = mutableListOf<String>()
        try {
            reader = BufferedReader(InputStreamReader(contentResolver.openInputStream(uri!!)))
            reader.forEachLine {

                //Checks that File only contains numbers
                if (!vm.isNumber(it)) {
                    Toast.makeText(
                        application.applicationContext,
                        "File Invalid, Values are not all numbers",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@forEachLine
                } else {
                    resultList.addAll(listOf(it))
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return vm.sortList(resultList)
    }

    private fun writeToFile(data: MutableList<String>, fileName: String) {
        verifyStoragePermissions(this)
        val outPutData: String = formatData(data)
        var outputStream: FileOutputStream? = null
        val path = "/storage/emulated/0/Download/"
        try {
            val textFile: File = File(path, fileName)
            outputStream = FileOutputStream(textFile)
            outputStream.write(outPutData.toByteArray())
            outputStream.close()
            Toast.makeText(this, "File Saved at $path output.txt", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            Log.i(TAG, e.message.toString())
            Toast.makeText(this, "Error Saving File", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatData(data: MutableList<String>): String {
        val outputText = StringBuilder()
        data.forEach { item ->
            outputText.append(item)
            outputText.append("\n")
        }
        return outputText.toString()
    }

    private fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }
}