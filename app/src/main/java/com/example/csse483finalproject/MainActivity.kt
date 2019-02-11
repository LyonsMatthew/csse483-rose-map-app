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
import android.util.Log
import android.util.TypedValue
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.csse483finalproject.map.data.MapData
import java.io.InputStream


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    EventAdapter.EventListListener, GroupsFragment.GroupListListener,
    MapFragment.MapDataHolder {
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
    lateinit var search: AutoCompleteTextView
    val mapDataMap: MutableMap<String, MapData> = HashMap<String, MapData>()


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

        loadAllMapData()
        val autoCompleteList: ArrayList<String> = ArrayList<String>()
        for (filename in mapDataMap.keys) {
            for (room in mapDataMap[filename]!!.roomMap.keys) {
                autoCompleteList.add(room + " : " + mapDataMap[filename]!!.name)
            }
        }
        search = findViewById(R.id.autoCompleteTextView)
        search.threshold = 1
        search.setAdapter(ArrayAdapter<String>(this, android.R.layout.select_dialog_item, autoCompleteList))

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
        return when (item.itemId) {
            R.id.action_search -> {
                searchForRoom()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
                setFragmentToMap("campus")
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

    fun setFragmentToMap(fileName: String) {
        var bundle: Bundle = Bundle()
        val newFragment = MapFragment()
        bundle.putString("fileName", fileName)
        bundle.putBoolean("isCampusMap", fileName == "campus")
        newFragment.arguments = bundle
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, newFragment)
        currentFragment.addToBackStack("campus")
        currentFragment.commit()
    }

    fun setFragmentToLocationShare() {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, LocationShareFragment.newInstance(alou))
        currentFragment.commit()
    }

    fun searchForRoom() {
        if (!search.text.toString().contains(" : ")) return
        var name = search.text.toString().split(" : ")[1]
//        Log.d(Constants.TAG, room)
        for (filename in mapDataMap.keys) {
            if (mapDataMap[filename]!!.name == name) {
                setFragmentToMap(filename)
            }
        }
    }

    fun loadAllMapData() {
        mapDataMap.put("apartments1", MapData("Apartments 1"))
        mapDataMap.put("apartments2", MapData("Apartments 2"))
        mapDataMap.put("bic", MapData("Branam Innovation Center"))
        mapDataMap.put("blumberg1", MapData("Blumberg 1"))
        mapDataMap.put("blumberg2", MapData("Blumberg 2"))
        mapDataMap.put("bsb0", MapData("Baur-Sames-Bogart Basement"))
        mapDataMap.put("bsb1", MapData("Baur-Sames-Bogart 1"))
        mapDataMap.put("bsb2", MapData("Baur-Sames-Bogart 2"))
        mapDataMap.put("bsb3", MapData("Baur-Sames-Bogart 3"))
        mapDataMap.put("cook_greenhouse", MapData("Cook Greenhouse"))
        mapDataMap.put("cook_stadium", MapData("Cook Stadium"))
        mapDataMap.put("crapo1", MapData("Crapo 1"))
        mapDataMap.put("crapo2", MapData("Crapo 2"))
        mapDataMap.put("crapo3", MapData("Crapo 3"))
        mapDataMap.put("dattic", MapData("Deming Attic"))
        mapDataMap.put("deming0", MapData("Deming 0"))
        mapDataMap.put("deming1", MapData("Deming 1"))
        mapDataMap.put("deming2", MapData("Deming 2"))
        mapDataMap.put("facilities", MapData("Facilities"))
        mapDataMap.put("hadley1", MapData("Hadley 1"))
        mapDataMap.put("hadley2", MapData("Hadley 2"))
        mapDataMap.put("hatfield0", MapData("Hatfield 0"))
        mapDataMap.put("hatfield1", MapData("Hatfield 1"))
        mapDataMap.put("hatfield2", MapData("Hatfield 2"))
        mapDataMap.put("lakeside1", MapData("Lakeside 1"))
        mapDataMap.put("lakeside2", MapData("Lakeside 2"))
        mapDataMap.put("lakeside3", MapData("Lakeside 3"))
        mapDataMap.put("lakeside4", MapData("Lakeside 4"))
        mapDataMap.put("logan1", MapData("Logan Library 1"))
        mapDataMap.put("logan2", MapData("Logan Library 2"))
        mapDataMap.put("logan3", MapData("Logan Library 3"))
        mapDataMap.put("mees1", MapData("Mees 1"))
        mapDataMap.put("mees2", MapData("Mees 2"))
        mapDataMap.put("moench1", MapData("Moench 1"))
        mapDataMap.put("moench2", MapData("Moench 2"))
        mapDataMap.put("moenchlow1", MapData("Moench Lower Level 1"))
        mapDataMap.put("moenchlow2", MapData("Moench Lower Level 2"))
        mapDataMap.put("myers1", MapData("Myers 1"))
        mapDataMap.put("myers2", MapData("Myers 2"))
        mapDataMap.put("oakley", MapData("Oakley Observatory"))
        mapDataMap.put("olin1", MapData("Olin 1"))
        mapDataMap.put("olin2", MapData("Olin 2"))
        mapDataMap.put("olinalc1", MapData("Olin Advanced Learning Center 1"))
        mapDataMap.put("olinalc2", MapData("Olin Advanced Learning Center 2"))
        mapDataMap.put("olinroof", MapData("Olin Roof"))
        mapDataMap.put("percopo0", MapData("Percopo 0"))
        mapDataMap.put("percopo1", MapData("Percopo 1"))
        mapDataMap.put("percopo2", MapData("Percopo 2"))
        mapDataMap.put("percopo3", MapData("Percopo 3"))
        mapDataMap.put("rotz", MapData("Rotz Mechanical Engineering Lab"))
        mapDataMap.put("scharp1", MapData("Scharpenberg 1"))
        mapDataMap.put("scharp2", MapData("Scharpenberg 2"))
        mapDataMap.put("skinner1", MapData("Skinner 1"))
        mapDataMap.put("skinner2", MapData("Skinner 2"))
        mapDataMap.put("speed0", MapData("Speed Basement"))
        mapDataMap.put("speed1", MapData("Speed 1"))
        mapDataMap.put("speed2", MapData("Speed 2"))
        mapDataMap.put("speed3", MapData("Speed 3"))
        mapDataMap.put("src1", MapData("Sports and Recreation Center 1"))
        mapDataMap.put("src2", MapData("Sports and Recreation Center 2"))
        mapDataMap.put("union0", MapData("Mussallem Union 0"))
        mapDataMap.put("union1", MapData("Mussallem Union 1"))
        mapDataMap.put("union2", MapData("Mussallem Union 2"))
        mapDataMap.put("ventures1", MapData("Ventures 1"))
        mapDataMap.put("ventures2", MapData("Ventures 2"))
        mapDataMap.put("whitechapel", MapData("White Chapel"))
        mapDataMap.put("campus", MapData("Rose-Hulman"))

        for (filename in mapDataMap.keys) {
            val inputStream: InputStream = assets.open("$filename.gpx")
            mapDataMap[filename]!!.readFile(inputStream)
        }
    }

    override fun getMapData(filename: String): MapData {
        return mapDataMap[filename]!!
    }

    override fun goToMap(filename: String) {
        setFragmentToMap(filename)
    }
}
