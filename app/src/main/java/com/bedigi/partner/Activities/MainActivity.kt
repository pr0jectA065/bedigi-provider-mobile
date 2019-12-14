package com.bedigi.partner.Activities

import android.content.DialogInterface
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import com.bedigi.partner.Fragment.Home
import com.bedigi.partner.Fragment.MyServices
import com.bedigi.partner.Fragment.Profile
import com.bedigi.partner.Preferences.AppPreferences
import com.bedigi.partner.Preferences.Utilities
import com.bedigi.partner.R
import com.google.android.material.navigation.NavigationView

import me.ibrahimsn.lib.SmoothBottomBar

class MainActivity : AppCompatActivity() {

    private var fragmentManager: FragmentManager? = null
    internal var fragmentTransaction: FragmentTransaction? = null
    private var fragment: Fragment? = null
    internal var toggle: ActionBarDrawerToggle? = null
    protected var appPrefs: AppPreferences? = null
    internal var navigationView: NavigationView? = null
    internal var doubleBackToExitPressedOnce = false

    internal var bottomBar: SmoothBottomBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setTitle("")

        bottomBar = findViewById<View>(R.id.bottomBar) as SmoothBottomBar

        bottomBar!!.onItemSelected = {
            Log.e("TAG", "Item $it selected")

            if (it == 0) {
                fragmentTransaction = fragmentManager!!.beginTransaction()
                fragment = Home()
                fragmentTransaction!!.replace(R.id.main_container, fragment!!)
                fragmentTransaction!!.commit()
            } else if (it == 1) {
                fragmentTransaction = fragmentManager!!.beginTransaction()
                fragment = MyServices()
                fragmentTransaction!!.replace(R.id.main_container, fragment!!)
                fragmentTransaction!!.commit()
            } else if (it == 2) {
                fragmentTransaction = fragmentManager!!.beginTransaction()
                fragment = Profile()
                fragmentTransaction!!.replace(R.id.main_container, fragment!!)
                fragmentTransaction!!.commit()
            }
        }

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        appPrefs = AppPreferences(this@MainActivity)

        drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        toggle = object : ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                supportInvalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                supportInvalidateOptionsMenu()
            }
        }

        drawer!!.setDrawerListener(toggle)
        (toggle as ActionBarDrawerToggle).syncState()

        fragmentManager = supportFragmentManager
        if (Utilities.checkNetworkConnection(this@MainActivity)) {

            bottomBar!!.setActiveItem(0)
            fragmentTransaction = fragmentManager!!.beginTransaction()
            fragment = Home()
            fragmentTransaction!!.replace(R.id.main_container, fragment!!)
            fragmentTransaction!!.commit()

        } else {
            Toast.makeText(this@MainActivity, "Please check your internet connection.", Toast.LENGTH_SHORT).show()
            finish()
        }

        navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView!!.itemIconTintList = null
        //View header = navigationView.inflateHeaderView(R.layout.nav_header);
        //TextView name = (TextView) header.findViewById(R.id.name);
        //name.setText("Welcome " + appPrefs.getProviderName());

        navigationView!!.setNavigationItemSelectedListener { item ->
            val id = item.itemId

            navigationView!!.clearFocus()
            navigationView!!.requestLayout()

            when (id) {

                R.id.history -> {
                    val mainIntent = Intent(this@MainActivity, Appointments::class.java)
                    startActivity(mainIntent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }

                R.id.privacyPolicy -> {
                    val mainIntent = Intent(this@MainActivity, PrivacyPolicy::class.java)
                    startActivity(mainIntent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }

                R.id.logout -> {
                    val builder = AlertDialog.Builder(this@MainActivity, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                    builder.setTitle(Html.fromHtml("<b>Logout</b>"))
                    builder.setMessage("Are you sure you want to logout?")
                    builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                        //appPrefs.RemoveAllSharedPreference()
                        //OneSignal.clearOneSignalNotifications();
                        Toast.makeText(this@MainActivity, "Logout successfully", Toast.LENGTH_SHORT).show()
                        val mainIntent = Intent(this@MainActivity, Login::class.java)
                        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(mainIntent)
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finish()
                    })
                    builder.setNegativeButton("Cancel", null)
                    builder.show()
                }

            }
            //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            assert(drawer != null)
            drawer!!.closeDrawer(GravityCompat.START)
            true
        }

        notifications = findViewById<View>(R.id.notifications) as ImageButton
        message = findViewById<View>(R.id.message) as ImageButton

        notifications!!.setOnClickListener {
          /*  *//*val mainIntent = Intent(this@MainActivity, Notifications::class.java)
            startActivity(mainIntent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)*/
        }

        message!!.setOnClickListener {
            val mainIntent = Intent(this@MainActivity, ChatList::class.java)
            startActivity(mainIntent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        location = findViewById<View>(R.id.location) as TextView

    }

    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                finishAffinity()
                overridePendingTransition(R.anim.fade_out, R.anim.fade_in)
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }

    }

    companion object {
        var drawer: DrawerLayout? = null
        var toolbar: Toolbar? = null

        var message: ImageButton? = null
        var notifications: ImageButton? = null
        var location: TextView? = null
    }

}
