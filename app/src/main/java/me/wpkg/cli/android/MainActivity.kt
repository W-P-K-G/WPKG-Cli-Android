@file:Suppress("DEPRECATION")

package me.wpkg.cli.android

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

import me.wpkg.cli.networking.UDPClient

import java.io.IOException

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnConnect = findViewById<Button>(R.id.btnConnect)
        val txtIP = findViewById<EditText>(R.id.txtIP)

        btnConnect.setOnClickListener { view: View? ->
            if (TextUtils.isEmpty(txtIP.text.toString()))
            {
                Snackbar.make(view!!, "Please enter ip", Snackbar.LENGTH_LONG).show()
            }
            else
            {
                val dialog = ProgressDialog.show(this@MainActivity, "", "Connecting. Please wait...", true)
                Thread {
                    try
                    {
                        val address = txtIP.text.toString().split(":".toRegex())
                        UDPClient.connect(address[0], address[1].toInt())
                        UDPClient.sendRegisterPing()
                        dialog.dismiss()
                        runOnUiThread { startActivity(Intent(this, WPKGManagerActivity::class.java)) }
                    }
                    catch (e: IOException)
                    {
                        runOnUiThread {
                            dialog.dismiss()
                            Snackbar.make(view!!, "Connection failed: " + e.message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }.start()
            }
        }
    }
}