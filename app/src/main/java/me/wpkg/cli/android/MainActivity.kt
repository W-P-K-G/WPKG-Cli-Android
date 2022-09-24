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
import androidx.lifecycle.lifecycleScope

import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

import java.io.IOException

import me.wpkg.cli.net.Client

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnConnect = findViewById<Button>(R.id.btnConnect)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)
        val txtIP = findViewById<EditText>(R.id.txtIP)

        val sharedPreferences = getPreferences(MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        txtPassword.setText(sharedPreferences.getString("password",""))

        btnConnect.setOnClickListener { view: View? ->
            if (TextUtils.isEmpty(txtIP.text.toString()))
            {
                Snackbar.make(view!!, "Please enter ip", Snackbar.LENGTH_LONG).show()
            }
            else
            {
                editor.putString("password",txtPassword.text.toString()).apply()

                val dialog = ProgressDialog.show(this@MainActivity, "", "Connecting. Please wait...", true)

                lifecycleScope.launch(Dispatchers.IO) {
                    try
                    {
                        val address = txtIP.text.toString().split(":".toRegex())
                        Client.connect(address[0], address[1].toInt())

                        dialog.dismiss()
                        when (Client.sendCommand("/registeradmin " + txtPassword.text.toString()))
                        {
                            "[REGISTER_SUCCESS]" -> runOnUiThread { startActivity(Intent(this@MainActivity, WPKGManagerActivity::class.java)) }
                            "[WRONG_PASSWORD]" -> Snackbar.make(view!!, "Wrong password.", Snackbar.LENGTH_LONG).show()
                            else -> Snackbar.make(view!!, "Register error.", Snackbar.LENGTH_LONG).show()
                        }
                    }
                    catch (e: IOException)
                    {
                        runOnUiThread {
                            dialog.dismiss()
                            Snackbar.make(view!!, "Connection failed: " + e.message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}