package com.to_do_app.to_do

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.to_do_app.to_do.fragments.SignInFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mAuth = FirebaseAuth.getInstance()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.main_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id=item.itemId
//        if(id==R.id.logout){
//            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
//            return true
//        }
//        return false
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logout){
            //write logic for logout
            mAuth.signOut()
            val intent = Intent(this@MainActivity,SignInFragment::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return true
    }
}