package com.lchowaniec.letsrunmate_final.Profile

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.lchowaniec.letsrunmate_final.Models.Activity
import com.lchowaniec.letsrunmate_final.Post.CommentsViewFragment
import com.lchowaniec.letsrunmate_final.Post.PostViewFragment
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.ProfileViewFragment

class ProfileActivity : AppCompatActivity(), PostViewFragment.CommentListener{
     override fun CommentListener(activity: Activity) {
        val fragment = CommentsViewFragment()
        val args = Bundle()
        args.putSerializable("Activity",activity)
        fragment.arguments = args
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container_profile,fragment)
        transaction.addToBackStack(getString(R.string.comments_view_fragment))
        transaction.commit()
    }

    private var ACTIVITY_NUM = 4
    private lateinit var imageProfile: ImageView
    var TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )
        init()


        //profile_progress_bar.visibility = View.GONE
        // val mToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.profile_app_bar)
        // setSupportActionBar(mToolbar)
        // setProfileImage()
        //  supportActionBar?.setTitle("Let's run mate!   ")
        //setupBottomNavigationBar()
//       profile_menu.setOnClickListener(){
        //     val intent = Intent(this,
        //         AccountSettingsActivity::class.java)
        //     startActivity(intent)



        }
    fun init(){
        val intent = getIntent()
        if(intent.hasExtra(getString(R.string.calling_activity))){
            if(intent.hasExtra(getString(R.string.user))){
                Log.d(TAG,"ProfileActivity: EXTRA EXSISTS")
                val fragment = ProfileViewFragment()
                val args = Bundle()
                args.putParcelable(getString(R.string.user),intent.getParcelableExtra(getString(R.string.user)))
                fragment.arguments = args
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container_profile,fragment)
                transaction.addToBackStack(getString(R.string.profile_view_fragment))
                transaction.commit()
            }else{
                Toast.makeText(this,"Something gone wrong",Toast.LENGTH_LONG).show()

            }



        }else{

            val fragment = ProfileFragment()
            val transaction: FragmentTransaction =
                this.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container_profile, fragment)
            transaction.commit()

        }






    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
    }
    }





    /*
    private fun setProfileImage(){
        val imgUrl= "https://androidworld89.files.wordpress.com/2011/11/314602_10150345480016699_209408956698_8369434_444124795_n.jpg"
        val mPhoto = findViewById<ImageView>(R.id.edit_profile_photo)
        ImageLoader().setImage(imgUrl,mPhoto,profile_progress_bar,"")

    }

    private fun setupBottomNavigationBar(){

        var bottomNaviBar : BottomNavigationView = findViewById(R.id.bottomNaviViewBar)
        BottomNaviViewHelper().setupBottomNaviView(bottomNaviBar)
        BottomNaviViewHelper().enableNavigationBar(baseContext,bottomNaviBar)
        val menu: Menu = bottomNaviBar.menu
        val menuItem: MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.setChecked(true)




    }
    */


