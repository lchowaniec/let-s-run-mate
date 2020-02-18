package com.lchowaniec.letsrunmate_final.Feed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.lchowaniec.letsrunmate_final.Models.Activity
import com.lchowaniec.letsrunmate_final.Post.CommentsViewFragment
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.FeedListAdapter


open class FeedActivity : AppCompatActivity(),FeedListAdapter.OnItemsListener{
    override fun onItem() {
        val fragment2 = supportFragmentManager.findFragmentById(R.id.feed_container) as FeedFragment

        if(fragment2!=null){
        fragment2!!.displayMoreActivities()
        }


    }

    private val ACTIVITY_NUM = 2
   // var mContext:Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )

        val fragment = FeedFragment()
        val transaction: FragmentTransaction =
            this.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.feed_container, fragment)
        transaction.commit()
}
    open fun onCommentListener(activity: Activity,callingActivity:String){
        val fragment = CommentsViewFragment()
        val bundle = Bundle()
        bundle.putParcelable(getString(R.string.activity),activity)
        bundle.putString(R.string.feed_activity.toString(),R.string.feed_activity.toString())
        fragment.arguments = bundle
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.feed_container,fragment)
        transaction.addToBackStack(R.string.comments_view_fragment.toString())
        transaction.commit()


    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
     //   finish()
    }
}

