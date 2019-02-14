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
import com.google.firebase.auth.FirebaseAuth
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
import android.widget.Toast
import edu.rosehulman.rosefire.Rosefire
import android.content.Intent


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    EventAdapter.EventListListener, GroupsFragment.GroupListListener,
    MapFragment.MapDataHolder, SplashFragment.OnLoginButtonPressedListener {
    override fun onCreateGroup() {
        var createdGroup = GroupWrapper.newTempGroup()
        createdGroup.wSetGroupName("Untitled Group")
        createdGroup.wSetMemberType(currentUser, MemberType(MT.OWNER))
        createdGroup.wSetIsHidden(false)
        createdGroup.wSetIsSingleUser(false)
        onGroupClicked(createdGroup)
    }

    override fun onCreateEvent() {
        var createdEvent = EventWrapper.newTempEvent()
        createdEvent.wSetEventName("Untitled Event")
        createdEvent.wSetGroupAccessLevel(GroupWithMembershipType(currentUser.wGetSingleUserGroup(),MemberType(MT.OWNER)),MemberType(MT.OWNER))
        onEventClicked(createdEvent)
    }

    fun onCreateEvent(locString: String) {
        var createdEvent = EventWrapper.newTempEvent()
        createdEvent.wSetEventLocation(Location("",false,0F,0F, locString))
        createdEvent.wSetEventName("Untitled Event")
        createdEvent.wSetGroupAccessLevel(GroupWithMembershipType(currentUser.wGetSingleUserGroup(),MemberType(MT.OWNER)),MemberType(MT.OWNER))
        onEventClicked(createdEvent)
    }

    override fun onGroupClicked(g: GroupWrapper) {
        if(g.wGetGroupOwners().containsUser(currentUser)){
            val currentFragment = supportFragmentManager.beginTransaction()
            currentFragment.replace(R.id.container, GroupDetailOwnerFragment.newInstance(g))
            currentFragment.addToBackStack("groupdetail")
            currentFragment.commit()
        }
        else{
            val currentFragment = supportFragmentManager.beginTransaction()
            currentFragment.replace(R.id.container, GroupDetailMemberFragment.newInstance(g))
            currentFragment.addToBackStack("groupdetail")
            currentFragment.commit()
        }
    }

    override fun onEventClicked(e: EventWrapper) {
        val filename = searchForRoom(e.wGetEventLocation().locName)
        val currentFragment = supportFragmentManager.beginTransaction()
        Log.d(Constants.TAG,"WGEO"+e.wGetEventOwners().toString())
        if (e.wGetEventOwners().containsUser(currentUser)){
            currentFragment.replace(R.id.container, EventDetailsOwnerFragment.newInstance(e, filename))
        }
        else {
            currentFragment.replace(R.id.container, EventDetailsFragment.newInstance(e, filename))
        }
        currentFragment.addToBackStack("eventdetail")
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
    var currentUser = UserWrapper.fromUser(User())
    var autoCompleteList = ArrayList<String>()

    val auth = FirebaseAuth.getInstance()
    lateinit var authListener: FirebaseAuth.AuthStateListener
    var uid = ""
    val REGISTRY_TOKEN = "25c8beb5-40ad-49d4-9135-bfd1d67dc38a"
    val RC_ROSEFIRE_LOGIN = 1001
    var username = ""
    var name = ""

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
        autoCompleteList = ArrayList<String>()
        for (filename in mapDataMap.keys) {
            for (room in mapDataMap[filename]!!.roomMap.keys) {
                autoCompleteList.add(room + " : " + mapDataMap[filename]!!.name)
            }
        }
        EventWrapper.locationAutoCompleteList = autoCompleteList
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
            processUserSnapshotChanges(querySnapshot!!)
        }

        initializeAuthListener()
        auth.addAuthStateListener(authListener)

        GroupWrapper.groupsRef=groupsRef
        EventWrapper.eventsRef=eventsRef
        //createUser("crenshch@rose-hulman.edu", "Connor Crenshaw")
        //createUser("tuser1@rose-hulman.edu", "Test User 1")
        //createUser("tuser2@rose-hulman.edu", "Test User 2")
        setFragmentToSplash()
    }

    fun createUser(username:String,displayName:String){
        Log.d(Constants.TAG, username + " " + displayName)
        usersRef.add(User(username,displayName)).addOnSuccessListener {
            var newUserRef = it
            it.get().addOnSuccessListener {
                var newUser = User.fromSnapshot(it)
                var uw = UserWrapper.fromUser(newUser)
                uw.wSetUsername(username)
                uw.wSetDisplayName(name)
                uw.saveToCloud()
                var ua = ArrayList<UserWrapper>()
                ua.add(uw)
                var userGroup = Group(displayName, MemberSpec(ua),MemberSpec(),true,false) //Create single-person group
                groupsRef.add(userGroup).addOnSuccessListener {
                    it.get().addOnSuccessListener {
                        newUser.singleUserGroup = GroupWrapper.fromGroup(Group.fromSnapshot(it))
                        newUserRef.set(newUser)
                    }
                }
                var lsGroup = Group(username+".locshare", MemberSpec(),MemberSpec(),false,true) //Create location share group
                groupsRef.add(lsGroup).addOnSuccessListener {
                    it.get().addOnSuccessListener {
                        newUser.locationShareGroup = GroupWrapper.fromGroup(Group.fromSnapshot(it))
                        newUserRef.set(newUser)
                    }
                }
            }
        }
    }

    fun onUserLoaded(){
        nav_view.getHeaderView(0).user_displayname.text = currentUser.wGetDisplayName()
        nav_view.getHeaderView(0).user_email.text = currentUser.wGetUsername()
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
        EventWrapper.setupEvents(masterEventsList)
        updateMyEventsList()
    }
    fun updateMyEventsList(){
        myEventsList.clear()
        for(e in masterEventsList){
            if (e.getUserAccessLevel(currentUser).mt != MT.NEITHER){
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
        GroupWrapper.setupGroups(masterGroupsList)
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
                    currentUser= UserWrapper.fromUser(user)
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
        UserWrapper.setupUsers(masterUsersList)
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
                val map = searchForRoom(search.editableText.toString())
                if (map != "") setFragmentToMap(map)
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
        currentFragment.addToBackStack("startup")
        currentFragment.commit()
    }

    fun setFragmentToEvents() {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, EventsFragment.newInstance(myEventsList))
        currentFragment.addToBackStack("events")
        currentFragment.commit()
    }

    fun setFragmentToGroups() {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, GroupsFragment.newInstance(filterAMGL(annotatedMasterGroupsList,false,false)))
        currentFragment.addToBackStack("groups")
        currentFragment.commit()
    }

    fun setFragmentToSplash() {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, SplashFragment())
        currentFragment.commit()
    }

    private fun filterAMGL(amgl: ArrayList<GroupWithMembershipType>, includeSingleMember: Boolean, includeHidden: Boolean): ArrayList<GroupWithMembershipType> {
        val out = ArrayList<GroupWithMembershipType>()
        Log.d(Constants.TAG, amgl.toString())
        for (gwmt in amgl){
            if (gwmt.membertype.mt!=MT.NEITHER && (includeSingleMember||!gwmt.group.wGetIsSingleUser()) && (includeHidden||!gwmt.group.wGetIsHidden())){
                out.add(gwmt)
                Log.d(Constants.TAG, gwmt.toString())
            }
        }
        return out
    }

    fun setFragmentToSettings() {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, SettingsFragment.newInstance(currentUser))
        currentFragment.addToBackStack("settings")
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
        currentFragment.addToBackStack("map")
        currentFragment.commit()
    }

    fun setFragmentToLocationShare() {
        val currentFragment = supportFragmentManager.beginTransaction()
        currentFragment.replace(R.id.container, LocationShareFragment.newInstance(currentUser.wGetLocationShareGroup()))
        currentFragment.addToBackStack("locshare")
        currentFragment.commit()
    }

    fun setFragmentToPrevious() {
        supportFragmentManager.popBackStackImmediate()
    }

    fun searchForRoom(text: String) : String {
        if (!text.contains(" : ")) return ""
        var name = text.split(" : ")[1]
//        Log.d(Constants.TAG, room)
        for (filename in mapDataMap.keys) {
            if (mapDataMap[filename]!!.name == name) {
                return filename
            }
        }
        return ""
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

    override fun makeEvent(room: String) {
        var locString = ""
        for (filename in mapDataMap.keys) {
            for (r in mapDataMap[filename]!!.roomMap.keys) {
                if (room == r) {
                    locString = room + " : " + mapDataMap[filename]!!.name
                }
            }
        }
        onCreateEvent(locString)
    }

    override fun onEventEditEnd() {
        setFragmentToEvents()
    }

    override fun onMapClick(filename: String) {
        setFragmentToMap(filename)
    }

    //authentication time let's go
    fun initializeAuthListener() {
        authListener = FirebaseAuth.AuthStateListener {
            val user = auth.currentUser
            if (user != null) {
                uid = user.uid
                onUserLoaded()
            } else {
                setFragmentToSplash()
            }
        }
    }

    fun onRosefireLogin() {
        val signInIntent = Rosefire.getSignInIntent(this, REGISTRY_TOKEN)
        startActivityForResult(signInIntent, RC_ROSEFIRE_LOGIN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_ROSEFIRE_LOGIN) {
            val result = Rosefire.getSignInResultFromIntent(data)
            username = result.email
            name = result.name
            createUser(username, name)

            if (!result.isSuccessful) {
                // The user cancelled the login
            }
            auth.signInWithCustomToken(result.token)
                .addOnCompleteListener(this) { task ->
                    Log.d(Constants.TAG, "signInWithCustomToken:onComplete:" + task.isSuccessful)

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // you should use an AuthStateListener to handle the logic for
                    // signed in user and a signed out user.
                    if (!task.isSuccessful) {
                        Log.w(Constants.TAG, "signInWithCustomToken", task.exception)
                        Toast.makeText(
                            this@MainActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }


    override fun onLoginButtonPressed() {
        onRosefireLogin()
    }

    fun logout() {
        auth.signOut()
    }
}
