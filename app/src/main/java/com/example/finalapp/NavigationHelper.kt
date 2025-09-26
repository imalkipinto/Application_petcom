package com.example.finalapp

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class NavigationHelper {
    
    companion object {
        fun setupNavigation(activity: Activity, currentPage: String) {
            val navigationBar = activity.findViewById<LinearLayout>(R.id.navigationBar)
            
            // Get all navigation items
            val navHome = navigationBar.findViewById<LinearLayout>(R.id.navHome)
            val navShop = navigationBar.findViewById<LinearLayout>(R.id.navShop)
            val navServices = navigationBar.findViewById<LinearLayout>(R.id.navServices)
            val navBlog = navigationBar.findViewById<LinearLayout>(R.id.navBlog)
            val navProfile = navigationBar.findViewById<LinearLayout>(R.id.navProfile)
            
            // Set active state based on current page
            setActiveState(activity, currentPage)
            
            // Set click listeners
            navHome.setOnClickListener {
                if (currentPage != "home") {
                    val intent = Intent(activity, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    activity.startActivity(intent)
                    activity.overridePendingTransition(0, 0)
                }
            }
            
            navShop.setOnClickListener {
                if (currentPage != "shop") {
                    val intent = Intent(activity, ShopActivity::class.java)
                    activity.startActivity(intent)
                    activity.overridePendingTransition(0, 0)
                }
            }
            
            navServices.setOnClickListener {
                if (currentPage != "services") {
                    val intent = Intent(activity, ServicesActivity::class.java)
                    activity.startActivity(intent)
                    activity.overridePendingTransition(0, 0)
                }
            }
            
            navBlog.setOnClickListener {
                if (currentPage != "blog") {
                    val intent = Intent(activity, BlogActivity::class.java)
                    activity.startActivity(intent)
                    activity.overridePendingTransition(0, 0)
                }
            }
            
            navProfile.setOnClickListener {
                if (currentPage != "profile") {
                    val intent = Intent(activity, UserProfileActivity::class.java)
                    activity.startActivity(intent)
                    activity.overridePendingTransition(0, 0)
                }
            }
        }
        
        private fun setActiveState(activity: Activity, currentPage: String) {
            val navigationBar = activity.findViewById<LinearLayout>(R.id.navigationBar)
            
            // Reset all to inactive state
            resetAllStates(navigationBar)
            
            // Set active state for current page
            when (currentPage) {
                "home" -> setActiveTab(navigationBar, R.id.navHome, R.id.iconHome, R.id.textHome)
                "shop" -> setActiveTab(navigationBar, R.id.navShop, R.id.iconShop, R.id.textShop)
                "services" -> setActiveTab(navigationBar, R.id.navServices, R.id.iconServices, R.id.textServices)
                "blog" -> setActiveTab(navigationBar, R.id.navBlog, R.id.iconBlog, R.id.textBlog)
                "profile" -> setActiveTab(navigationBar, R.id.navProfile, R.id.iconProfile, R.id.textProfile)
            }
        }
        
        private fun resetAllStates(navigationBar: LinearLayout) {
            val tabs = listOf(
                Triple(R.id.navHome, R.id.iconHome, R.id.textHome),
                Triple(R.id.navShop, R.id.iconShop, R.id.textShop),
                Triple(R.id.navServices, R.id.iconServices, R.id.textServices),
                Triple(R.id.navBlog, R.id.iconBlog, R.id.textBlog),
                Triple(R.id.navProfile, R.id.iconProfile, R.id.textProfile)
            )
            
            tabs.forEach { (_, iconId, textId) ->
                navigationBar.findViewById<ImageView>(iconId)?.setColorFilter(
                    android.graphics.Color.parseColor("#666666")
                )
                navigationBar.findViewById<TextView>(textId)?.setTextColor(
                    android.graphics.Color.parseColor("#666666")
                )
            }
        }
        
        private fun setActiveTab(navigationBar: LinearLayout, tabId: Int, iconId: Int, textId: Int) {
            navigationBar.findViewById<ImageView>(iconId)?.setColorFilter(
                android.graphics.Color.parseColor("#4ECDC4")
            )
            navigationBar.findViewById<TextView>(textId)?.setTextColor(
                android.graphics.Color.parseColor("#4ECDC4")
            )
        }
    }
}
