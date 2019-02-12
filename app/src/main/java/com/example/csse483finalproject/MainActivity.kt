package com.example.csse483finalproject

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.csse483finalproject.event.*
import com.example.csse483finalproject.group.*
import com.example.csse483finalproject.map.MapFragment
import com.example.csse483finalproject.map.data.MapData
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    EventAdapter.EventListListener, GroupsFragment.GroupListListener,
    MapFragment.MapDataHolder{
    override fun onGroupClicked(g: GroupWrapper) {
        if(g.getGroupOwners().containsUser(currentUser)){
            val currentFragment = supportFragmentManager.beginTransaction()
            currentFragment.replace(R.id.container, GroupDetailOwnerFragment.newInstance(g))
            currentFragment.commit()
        }
        else{
            val currentFragment = supportFragmentManager.beginTransaction()
            currentFragment.replace(R.id.container, GroupDetailMemberFragment.newInstance(g))
            currentFragment.commit()
        }
    }

    override fun onEventClicked(e: EventWrapper) {
        val currentFragment = supportFragmentManager.beginTransaction()
        if (e.getEventOwners().containsUser(currentUser)){
            currentFragment.replace(R.id.container, EventDetailsOwnerFragment.newInstance(e))
        }
        else {
            currentFragment.replace(R.id.container, EventDetailsFragment.newInstance(e))
        }
        currentFragment.commit()
    }

    lateinit var eventsRef: CollectionReference
    lateinit var groupsRef: CollectionReference
    lateinit var usersRef: CollectionReference


    lateinit var search: AutoCompleteTextView
    val mapDataMap: MutableMap<String, MapData> = HashMap<String, MapData>()
    val masterEventsList = ArrayList<Event>()
    var myEventsList = ArrayList<EventWrapper>()
    val masterGroupsList = ArrayList<Group>()
    val masterUsersList = ArrayList<User>()
    var annotatedMasterGroupsList = ArrayList<GroupWithMembershipType>()
    var currentUser = UserWrapper.fromUser(User()) // Todo: Add test users to database

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

        eventsRef = FirebaseFirestore
            .getInstance()
            .collection("events")

        eventsRef.addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                Log.w(Constants.TAG, "listen error", e)
                return@addSnapshotListener
            }
            processEventSnapshotChanges(querySnapshot!!)
        }

        groupsRef = FirebaseFirestore
            .getInstance()
            .collection("groups")

        groupsRef.addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                Log.w(Constants.TAG, "listen error", e)
                return@addSnapshotListener
            }
            processGroupSnapshotChanges(querySnapshot!!)
        }

        usersRef = FirebaseFirestore
            .getInstance()
            .collection("users")

        usersRef.addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                Log.w(Constants.TAG, "listen error", e)
                return@addSnapshotListener
            }
            processGroupSnapshotChanges(querySnapshot!!)
        }

        setFragmentToStartup()
    }

    fun onUserLoaded(){
        nav_view.getHeaderView(0).user_displayname.text = currentUser.getDisplayName()
        nav_view.getHeaderView(0).user_email.text = currentUser.getUsername()
        updateMyEventsList()
        updateAnnotatedMasterGroups()
        setFragmentToStartup()
    }

    private fun processEventSnapshotChanges(querySnapshot: QuerySnapshot) {
        // Snapshots has documents and documentChanges which are flagged by type,
        // so we can handle C,U,D differently.
        for (documentChange in querySnapshot.documentChanges) {
            val event = Event.fromSnapshot(documentChange.document)
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    Log.d(Constants.TAG, "Adding $event")
                    masterEventsList.add(0, event)
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removing $event")
                    for ((k, e) in masterEventsList.withIndex()) {
                        if (e.id == event.id) {
                            masterEventsList.removeAt(k)
                            break
                        }
                    }
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying $event")
                    for ((k, e) in masterEventsList.withIndex()) {
                        if (e.id == event.id) {
                            masterEventsList[k] = event
                            break
                        }
                    }
                }
            }
        }
        updateMyEventsList()
    }
    fun updateMyEventsList(){
        myEventsList.clear()
        for(e in masterEventsList){
            if (e.getAccessLevel(currentUser).mt != MT.NEITHER){
                myEventsList.add(EventWrapper.fromEvent(e))
            }
        }
    }


    private fun processGroupSnapshotChanges(querySnapshot: QuerySnapshot) {
        // Snapshots has documents and documentChanges which are flagged by type,
        // so we can handle C,U,D differently.
        for (documentChange in querySnapshot.documentChanges) {
            val group = Group.fromSnapshot(documentChange.document)
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    Log.d(Constants.TAG, "Adding $group")
                    masterGroupsList.add(0, group)
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removing $group")
                    for ((k, g) in masterGroupsList.withIndex()) {
                        if (g.id == group.id) {
                            masterGroupsList.removeAt(k)
                            break
                        }
                    }
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying $group")
                    for ((k, g) in masterGroupsList.withIndex()) {
                        if (g.id == group.id) {
                            masterGroupsList[k] = group
                            break
                        }
                    }
                }
            }
        }
        updateAnnotatedMasterGroups()
    }
    fun updateAnnotatedMasterGroups(){
        annotatedMasterGroupsList.clear()
        for (g in masterGroupsList){
            annotatedMasterGroupsList.add(GroupWithMembershipType(GroupWrapper.fromGroup(g),g.getMemberType(currentUser)))
        }
    }
    private fun processUserSnapshotChanges(querySnapshot: QuerySnapshot) {
        // Snapshots has documents and documentChanges which are flagged by type,
        // so we can handle C,U,D differently.
        for (documentChange in querySnapshot.documentChanges) {
            val user = User.fromSnapshot(documentChange.document)
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    Log.d(Constants.TAG, "Adding $user")
                    masterUsersList.add(0, user)
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removing $user")
                    for ((k, u) in masterUsersList.withIndex()) {
                        if (u.id == user.id) {
                            masterUsersList.removeAt(k)
                            break
                        }
                    }
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying $user")
                    for ((k, u) in masterUsersList.withIndex()) {
                        if (u.id == user.id) {
                            masterUsersList[k] = user
                            break
                        }
                    }
                }
            }
        }
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
        currentFragment.replace(R.id.container, StartupFragment.newInstance(currentUser))
        currentFragment.commit()
    }

    fun setFragmentToEvents() {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, EventsFragment.newInstance(myEventsList))
        currentFragment.commit()
    }

    fun setFragmentToGroups() {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, GroupsFragment.newInstance(annotatedMasterGroupsList))
        currentFragment.commit()
    }

    fun setFragmentToSettings() {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, SettingsFragment.newInstance(currentUser))
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
        currentFragment.replace(R.id.container, LocationShareFragment.newInstance(currentUser.getLocationShareGroup().getMembers(
            MemberType(MT.BOTH)
        )))
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
