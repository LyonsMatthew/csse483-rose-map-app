package com.example.csse483finalproject

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.csse483finalproject.event.Event
import com.example.csse483finalproject.event.EventDetailsFragment
import com.example.csse483finalproject.event.EventsFragment
import com.example.csse483finalproject.event.Location
import com.example.csse483finalproject.group.*
import com.example.csse483finalproject.map.MapFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, EventsFragment.EventListListener {

    override fun onEventClicked(e: Event) {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, EventDetailsFragment.newInstance(e))
        currentFragment.commit()
    }

    lateinit var testEvents:ArrayList<Event>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        testEvents = ArrayList<Event>()
        var testUser = User(0,"crenshch@rose-hulman.edu")
        var testMembers = ArrayList<User>()
        testMembers.add(testUser)
        var testOwnerSpec = Memberspec(testMembers)
        var testViewerSpec = Memberspec(ArrayList<User>())
        var testGroup = Group(testOwnerSpec,testViewerSpec,0)
        var testgArr=ArrayList<Group>()
        testgArr.add(testGroup)
        var testgMTarr = ArrayList<Membertype>()
        testgMTarr.add(Membertype.OWNER)
        var testgVTarr = ArrayList<Membertype>()
        testgVTarr.add(Membertype.VIEWER)
        var testOwner = Groupspec(testgArr,testgMTarr)
        var testViewer = Groupspec(testgArr,testgMTarr)
        testEvents.add(Event("Test RoseMaps", Location(null,false,0F,0F, "Lakeside 402"),"Rose maps is great", Date(119,0,31,8,30), Date(119,0,31,9,30),testOwner, testViewer,0))
        testEvents.add(Event("Making events manually is a real pain...", Location(null,false,0F,0F, "Speed Beach"),"Manual event", Date(119,0,31,8,30), Date(119,0,31,9,30),testOwner, testViewer,1))

        setFragmentToStartup()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_search -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_events -> {
                setFragmentToEvents()
            }
            R.id.nav_groups -> {
                setFragmentToGroups()
            }
            R.id.nav_settings -> {
                setFragmentToSettings()
            }
            R.id.nav_map -> {
                setFragmentToMap("Crapo 2", "crapo2")
            }
            R.id.nav_locshare -> {
                setFragmentToLocationShare()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun setFragmentToStartup() {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, StartupFragment())
        currentFragment.commit()
    }

    fun setFragmentToEvents() {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, EventsFragment.newInstance(testEvents))
        currentFragment.commit()
    }

    fun setFragmentToGroups() {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, GroupsFragment())
        currentFragment.commit()
    }

    fun setFragmentToSettings() {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, SettingsFragment())
        currentFragment.commit()
    }

    fun setFragmentToMap(name: String, fileName: String) {
        var bundle: Bundle = Bundle()
        val newFragment = MapFragment()
        bundle.putString("name", name)
        bundle.putString("fileName", fileName)
        newFragment.arguments = bundle
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, newFragment)
        currentFragment.commit()
    }

    fun setFragmentToLocationShare() {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, LocationShareFragment())
        currentFragment.commit()
    }
}
