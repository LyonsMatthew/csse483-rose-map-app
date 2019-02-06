package com.example.csse483finalproject

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.csse483finalproject.event.*
import com.example.csse483finalproject.group.*
import com.example.csse483finalproject.map.MapFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.util.*
import kotlin.collections.ArrayList
import android.R.attr.data
import android.util.TypedValue



class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    EventAdapter.EventListListener, GroupsFragment.GroupListListener {
    override fun onGroupClicked(g: GroupWithMembershipType) {
        if(g.membertype.mt == MT.OWNER || g.membertype.mt == MT.BOTH){
            val currentFragment = supportFragmentManager.beginTransaction()
            currentFragment.replace(R.id.container, GroupDetailOwnerFragment.newInstance(g.group))
            currentFragment.commit()
        }
        else{
            val currentFragment = supportFragmentManager.beginTransaction()
            currentFragment.replace(R.id.container, GroupDetailMemberFragment.newInstance(g.group))
            currentFragment.commit()
        }
    }

    override fun onEventClicked(e: Event) {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, EventDetailsFragment.newInstance(e))
        currentFragment.commit()
    }

    lateinit var testEvents:ArrayList<Event>
    lateinit var testUser: User
    lateinit var testMembers: ArrayList<User>
    lateinit var testOwnerSpec: MemberSpec
    lateinit var testViewerSpec: MemberSpec
    lateinit var testGroup: Group
    lateinit var gwmto: GroupWithMembershipType
    lateinit var gwmtv: GroupWithMembershipType
    lateinit var gwmtoa: ArrayList<GroupWithMembershipType>
    lateinit var gwmtov: ArrayList<GroupWithMembershipType>
    lateinit var testOwner: GroupSpec
    lateinit var testViewer: GroupSpec
    lateinit var alou: ArrayList<User>


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

        //Code to find actionbar height taken from the following StackOverflow post:
        //https://stackoverflow.com/questions/12301510/how-to-get-the-actionbar-height
        //Credit to Anthony
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            Constants.ACTIONBAR_HEIGHT = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics) + 60
        }

        testEvents = ArrayList<Event>()
        testUser = User(0,"crenshch@rose-hulman.edu", "Connor Crenshaw")
        testMembers = ArrayList<User>()
        testMembers.add(testUser)
        testOwnerSpec = MemberSpec(testMembers)
        testViewerSpec = MemberSpec(ArrayList<User>())
        testGroup = Group("Test Users",testOwnerSpec,testViewerSpec,0)
        gwmto = GroupWithMembershipType(testGroup, MemberType(MT.OWNER))
        gwmtv = GroupWithMembershipType(testGroup, MemberType(MT.VIEWER))
        gwmtoa = ArrayList<GroupWithMembershipType>()
        gwmtoa.add(gwmto)
        gwmtov = ArrayList<GroupWithMembershipType>()
        gwmtov.add(gwmtv)
        testOwner = GroupSpec(gwmtoa)
        testViewer = GroupSpec(gwmtov)
        alou=ArrayList<User>()
        alou.add(testUser)
        testEvents.add(Event("Test RoseMaps", Location(null,false,0F,0F, "Lakeside 402"),"Rose maps is great", Date(119,0,31,8,30), Date(119,0,31,9,30),testOwner, testViewer,0))
        testEvents.add(Event("Making events manually is a real pain...", Location(null,false,0F,0F, "Speed Beach"),"Manual event", Date(119,0,31,8,30), Date(119,0,31,9,30),testOwner, testViewer,1))
        nav_view.getHeaderView(0).user_displayname.text = testUser.displayName
        nav_view.getHeaderView(0).user_email.text = testUser.username
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
//                setFragmentToMap("Crapo 2", "facilities")
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
        currentFragment.replace(R.id.container, GroupsFragment.newInstance(gwmtov))
        currentFragment.commit()
    }

    fun setFragmentToSettings() {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, SettingsFragment.newInstance(testUser))
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
        currentFragment.replace(R.id.container, LocationShareFragment.newInstance(alou))
        currentFragment.commit()
    }
}
