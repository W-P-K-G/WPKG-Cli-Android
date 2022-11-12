package me.wpkg.cli.android

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.wpkg.cli.net.Client
import me.wpkg.cli.net.Protocol
import java.io.IOException


class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnConnect = findViewById<Button>(R.id.btnConnect)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)
        val txtIP = findViewById<TextView>(R.id.txtIP)

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

                val connectdialog = ProgressDialog(this@MainActivity, "", "Connecting. Please wait...")
                connectdialog.show()

                lifecycleScope.launch(Dispatchers.IO) {
                    try
                    {
                        val address = txtIP.text.toString().split(":".toRegex())
                        Client.setProtocol(Protocol.TCP)
                        Client.connect(address[0], address[1].toInt())

                        connectdialog.dismiss()
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
                            connectdialog.dismiss()
                            Snackbar.make(view!!, "Connection failed: " + e.message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}