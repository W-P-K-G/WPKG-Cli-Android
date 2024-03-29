package me.wpkg.cli.android

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import me.wpkg.cli.net.Client
import me.wpkg.cli.utils.Utils

import java.io.IOException

class ClientManagerActivity : AppCompatActivity()
{
    @JvmField
    var commandWorks: Boolean = false
    @JvmField
    var statsRefreshing = false

    var joined = false

    private lateinit var adapter: TabAdapter

    lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_manager)
        val bundle = intent.extras

        // calling the action bar
        val actionBar = supportActionBar

        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = bundle!!.getString("name")
        }
        tabLayout = findViewById(R.id.tabLayoutClient)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager2Client)

        adapter = TabAdapter(this, supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        val tabs = arrayOf("Commands", "Stats")

        TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, position: Int ->
            tab.text = tabs[position]
        }.attach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if (item.itemId == android.R.id.home)
        {
            disconnect()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onStart()
    {
        super.onStart()
        joined = true
    }

    override fun onStop()
    {
        super.onStop()
        joined = false
    }

    override fun onBackPressed()
    {
        super.onBackPressed()
        disconnect()
    }

    private fun disconnect()
    {
        joined = false
        lifecycleScope.launch(Dispatchers.IO) {
            try
            {
                Client.sendCommand("/unjoin")
            }
            catch (e: IOException)
            {
                Utils.errorSnakeBar(window.decorView.rootView, e)
            }
            runOnUiThread { finish() }
        }
    }
}

class TabAdapter(var parent: ClientManagerActivity, fragmentManager: FragmentManager?, lifecycle: Lifecycle?) :
    FragmentStateAdapter(
        fragmentManager!!, lifecycle!!
    )
{
    private val commandFragment = CommandFragment.newInstance()
    private val statsFragment = StatsFragment.newInstance()

    override fun createFragment(position: Int): Fragment
    {
        return when (position)
        {
            0 -> commandFragment
            1 -> statsFragment
            else -> Fragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}