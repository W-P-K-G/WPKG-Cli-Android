package me.wpkg.cli.android

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import me.wpkg.cli.json.JsonMaps.ClientMap
import me.wpkg.cli.json.JsonMaps.ClientObject
import me.wpkg.cli.net.Client
import me.wpkg.cli.utils.Utils

import java.io.IOException

class WPKGManagerActivity : AppCompatActivity(), View.OnClickListener, ClientSelectedListener
{

    lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClientAdapter
    private lateinit var btnLogoff: Button
    private lateinit var btnRefresh: Button
    private lateinit var txtNoClients: TextView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wpkgmanager)

        btnLogoff = findViewById(R.id.btnDisconnect)
        btnLogoff.setOnClickListener(this)

        btnRefresh = findViewById(R.id.btnRefresh)
        btnRefresh.setOnClickListener(this)
        txtNoClients = findViewById(R.id.txtNoClients)

        val clients = ArrayList<ClientObject>()
        adapter = ClientAdapter(clients, this)

        recyclerView = findViewById(R.id.clientRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    public override fun onStart()
    {
        super.onStart()
        refreshList()
    }

    override fun onBackPressed()
    {
        super.onBackPressed()
        logOff()
    }

    override fun onClick(source: View)
    {
        if (source === btnLogoff)
        {
            logOff()
        }
        else if (source === btnRefresh)
        {
            refreshList()
            Toast.makeText(source.getContext(), "Client list refreshed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshList()
    {
        adapter.clear()
        txtNoClients.visibility = View.INVISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            try
            {
                val map = getClientList(Client.sendCommand("/rat-list"))
                if (map.clients.isEmpty())
                {
                    runOnUiThread { txtNoClients.visibility = View.VISIBLE }
                    return@launch
                }
                for (client in map.clients)
                    runOnUiThread { adapter.addItem(client!!) }

            }
            catch (e: IOException)
            {
                Utils.errorSnakeBar(window.decorView.rootView, e)
            }
        }
    }

    private fun logOff()
    {
        lifecycleScope.launch(Dispatchers.IO) {
            Client.logOff()
            runOnUiThread { finish() }
        }
    }

    private fun getClientList(json: String?): ClientMap
    {
        val objectMapper = ObjectMapper()
        lateinit var clientJSON: ClientMap

        try
        {
            clientJSON = objectMapper.readValue(json, ClientMap::class.java)
        }
        catch (e: JsonProcessingException)
        {
            Snackbar.make(window.decorView.rootView, "JSON parsing failed: " + e.message, Snackbar.LENGTH_SHORT).show()
        }
        return clientJSON
    }

    override fun clientSelected(client: ClientObject)
    {
        if (client.joined)
            Snackbar.make(window.decorView.rootView, "Client is already joined.", Snackbar.LENGTH_SHORT).show()
        else
        {
            lifecycleScope.launch(Dispatchers.IO) {
                try
                {
                    Client.sendCommand("/join " + client.id)

                    val intent = Intent(this@WPKGManagerActivity, ClientManagerActivity::class.java)
                    intent.putExtra("name", client.name)
                    runOnUiThread { startActivity(intent) }
                }
                catch (e: IOException)
                {
                    Utils.errorSnakeBar(window.decorView.rootView, e)
                }
            }
        }
    }

    override fun clientMenuInvoked(client: ClientObject, layout: ConstraintLayout)
    {
        val popup = PopupMenu(this, layout)
        popup.menuInflater.inflate(R.menu.client_popup_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            if (menuItem.itemId == R.id.menuItemKill)
            {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Kill")
                builder.setMessage("Are you want to kill " + client.name + "?")
                builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                    lifecycleScope.launch(Dispatchers.IO) {
                        try
                        {
                            Client.sendCommand("/close " + client.id)
                            runOnUiThread {
                                Toast.makeText(this@WPKGManagerActivity, "Client killed!", Toast.LENGTH_SHORT).show()
                                refreshList()
                            }
                        }
                        catch (e: IOException)
                        {
                            Utils.errorSnakeBar(window.decorView.rootView, e)
                        }
                    }
                }
                builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int -> dialog.dismiss() }

                val alert = builder.create()
                alert.show()
            }
            true
        }
        popup.show()
    }
}