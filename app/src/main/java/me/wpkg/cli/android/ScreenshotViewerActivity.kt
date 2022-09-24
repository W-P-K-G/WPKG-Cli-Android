package me.wpkg.cli.android

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import me.wpkg.cli.utils.Utils

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class ScreenshotViewerActivity : AppCompatActivity()
{
    private lateinit var imgScreenshot: ImageView
    private lateinit var progressBar: ProgressBar

    private var url: String? = ""
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screenshot_viewer)

        // calling the action bar
        val actionBar = supportActionBar

        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Screenshot"
        }
        imgScreenshot = findViewById(R.id.imgScreenshot)
        progressBar = findViewById(R.id.proScreenshot)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.screenshot_share_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
        {
            finish()
        }
        if (item.itemId == R.id.btnScreenshotShare)
        {
            if (url == "")
                Toast.makeText(this, "Screenshot not loaded!", Toast.LENGTH_SHORT).show()
            else
            {
                try
                {
                    val screenshot = SharedScreenshot(bitmap, this)
                    val sharingIntent = Intent(Intent.ACTION_SEND)

                    sharingIntent.type = "image/png"
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshot.uri)
                    startActivity(Intent.createChooser(sharingIntent, "Share screenshot"))

                    screenshot.destroy()
                }
                catch (e: IOException)
                {
                    Utils.errorSnakeBar(window.decorView.rootView, e)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart()
    {
        super.onStart()
        val bundle = intent.extras
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try
            {
                val url = bundle!!.getString("url")

                val input = URL(bundle.getString("url")).openStream()
                bitmap = BitmapFactory.decodeStream(input)
                this@ScreenshotViewerActivity.url = url

                runOnUiThread {
                    imgScreenshot.setImageBitmap(bitmap)
                    progressBar.visibility = View.INVISIBLE
                }
            }
            catch (e: Exception)
            {
                Utils.errorSnakeBar(window.decorView.rootView, e)
            }
        }
    }

    inner class SharedScreenshot(bitmap: Bitmap, context: Context)
    {
        var uri: Uri
        private var imageFolder = File(cacheDir, "wpkgScreenshots")
        private var screenshot: File

        init
        {
            imageFolder.mkdir()
            screenshot = File(imageFolder, "wpkgScreenshot.png")

            val outputStream = FileOutputStream(screenshot)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
            outputStream.flush()
            outputStream.close()
            uri = FileProvider.getUriForFile(context, "com.anni.shareimage.fileprovider", screenshot)
        }

        fun destroy()
        {
            imageFolder.deleteOnExit()
            screenshot.deleteOnExit()
        }
    }
}