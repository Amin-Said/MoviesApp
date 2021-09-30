package com.amin.moviesapp.utils.extensions

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import android.view.animation.BaseInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi

import androidx.recyclerview.widget.RecyclerView
import com.amin.moviesapp.R
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.getString(id: Int) =
    this.getString(id)

fun Context.toastFromResource(resourceID:Int) =
    Toast.makeText(this, this.getString(resourceID), Toast.LENGTH_SHORT).show()

@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
fun RecyclerView.ViewHolder.addAnimation(interpolator: BaseInterpolator, lastPosition: Int, duration:Long){
    var lastPosition = -1
    val delayTime: Long = 200
    this.itemView.visibility = View.INVISIBLE
    if (this.layoutPosition > lastPosition) {
        this.itemView.handler.postDelayed(Runnable {
            this.itemView.visibility = View.VISIBLE
            val alpha = ObjectAnimator.ofFloat(this.itemView, "alpha", 0f, 1f)
            val scaleY =
                ObjectAnimator.ofFloat(this.itemView, "scaleY", 0f, 1f)
            val scaleX =
                ObjectAnimator.ofFloat(this.itemView, "scaleX", 0f, 1f)
            val animSet = AnimatorSet()
            animSet.play(alpha).with(scaleY).with(scaleX)
            animSet.interpolator = interpolator
            animSet.duration = duration
            animSet.start()
        }, delayTime)
        lastPosition = this.layoutPosition
    } else {
        this.itemView.visibility = View.VISIBLE
    }


}



fun ImageView.setImageWithPicasso(sourceUrl:String){
    val imageView = this
    Picasso.get()
        .load(sourceUrl)
        .placeholder(R.drawable.progress_animation)
        .error(R.drawable.error)
        .networkPolicy(NetworkPolicy.OFFLINE)
        .into(imageView, object : Callback {
            override fun onSuccess() {}
            override fun onError(e: Exception) {
                //Try again online if cache failed
                e.printStackTrace()
                Log.v("Picasso","Could not fetch image ${e.message}");
                Picasso.get()
                    .load(sourceUrl)
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.error)
                    .into(imageView, object : Callback {
                        override fun onSuccess() {}
                        override fun onError(e: Exception) {
                            e.printStackTrace()
                            Log.v("Picasso","Could not fetch image ${e.message}");

                        }
                    })
            }
        })


}

fun Context.isOnline(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
    }
    return false
}