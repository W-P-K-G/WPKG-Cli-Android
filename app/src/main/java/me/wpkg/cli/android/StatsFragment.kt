@file:Suppress("DEPRECATION")

package me.wpkg.cli.android

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.wpkg.cli.commands.error.ErrorHandler
import me.wpkg.cli.net.Client
import me.wpkg.cli.utils.Utils
import java.io.IOException

class StatsFragment : Fragment()
{
    var parent: ClientManagerActivity? = activity as ClientManagerActivity?

    private lateinit var proClientCpu: ProgressBar
    private lateinit var proClientRam: ProgressBar
    private lateinit var proClientSwap: ProgressBar
    private lateinit var txtClientCpu: TextView
    private lateinit var txtClientSwap: TextView
    private lateinit var txtClientRam: TextView

    private lateinit var errorHandler: ErrorHandler

    override fun onStart()
    {
        super.onStart()
        parent = activity as ClientManagerActivity?

        refresh()
        statsThread()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)
        proClientCpu = view.findViewById(R.id.proClientCpu)
        proClientRam = view.findViewById(R.id.proClientRam)
        proClientSwap = view.findViewById(R.id.proClientSwap)

        txtClientCpu = view.findViewById(R.id.txtClientCpu)
        txtClientRam = view.findViewById(R.id.txtClientRam)
        txtClientSwap = view.findViewById(R.id.txtClientSwap)

        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.layoutSwipeRefresh)
        swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch(Dispatchers.IO) {
                refreshStats()
                parent!!.runOnUiThread { swipeRefreshLayout.isRefreshing = false }
            }
        }

        //error handler
        errorHandler = ErrorHandler()
        errorHandler.setSessionExpiredEvent {
            parent!!.runOnUiThread {
                Toast.makeText(parent!!.window.context, "Client disconnected. Session expired.", Toast.LENGTH_LONG)
                    .show()
                parent!!.finish()
            }
        }

        errorHandler.setNotAuthorizedEvent {
            parent!!.runOnUiThread {
                Toast.makeText(parent!!.window.context, "Server password expired", Toast.LENGTH_LONG)
                    .show()
                val i = Intent(parent, MainActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                parent!!.startActivity(i)
            }
        }

        return view
    }

    private fun refresh()
    {
        val dialog = ProgressDialog(parent, "", "Refreshing. Please wait...")
        dialog.show()
        lifecycleScope.launch(Dispatchers.IO) {
            refreshStats()
            parent!!.runOnUiThread { dialog.dismiss() }
        }
    }

    private fun statsThread()
    {
        Thread {
            Utils.sleep(10000)
            while (true) {
                if (!parent!!.joined)
                    return@Thread
                if (!parent!!.commandWorks && parent!!.tabLayout.selectedTabPosition == 1)
                    refreshStats()

                Utils.sleep(10000)
            }
        }.start()
    }

    @SuppressLint("SetTextI18n")
    fun refreshStats()
    {
        try
        {
            parent!!.statsRefreshing = true
            val mess = errorHandler.check(Client.sendCommand("stat")).split(" ".toRegex())

            if (errorHandler.ok())
            {
                val cpu = Math.floor(mess[0].toFloat().toDouble()).toInt()
                val memfree = Utils.roundTo2DecimalPlace(mess[1].toDouble() / 1024 / 1024 / 1024)
                val memtotal = Utils.roundTo2DecimalPlace(mess[2].toDouble() / 1024 / 1024 / 1024)
                val swapfree = Utils.roundTo2DecimalPlace(mess[3].toDouble() / 1024 / 1024 / 1024)
                val swaptotal = Utils.roundTo2DecimalPlace(mess[4].toDouble() / 1024 / 1024 / 1024)
                parent!!.runOnUiThread {
                    proClientCpu.progress = cpu
                    txtClientCpu.text = "$cpu%"

                    txtClientRam.text = memfree.toString() + "GB/" + memtotal + "GB"
                    proClientRam.progress = (memfree * 100 / memtotal).toInt()

                    txtClientSwap.text = swapfree.toString() + "GB/" + swaptotal + "GB"
                    proClientSwap.progress = (swapfree * 100 / swaptotal).toInt()
                }
                parent!!.statsRefreshing = false
            }
            else return
        }
        catch (e: IOException)
        {
            Utils.errorSnakeBar(requireActivity().findViewById(android.R.id.content), e)
        }
    }

    companion object
    {
        fun newInstance(): StatsFragment
        {
            return StatsFragment()
        }
    }
}