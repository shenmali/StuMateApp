package com.shenmali.stumateapp.ui.home.matchrequests

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.shenmali.stumateapp.ui.home.matchrequests.received.MatchRequestsReceivedPageFragment
import com.shenmali.stumateapp.ui.home.matchrequests.sent.MatchRequestsSentPageFragment
import com.shenmali.stumateapp.ui.home.matchrequests.matched.MatchRequestsMatchedPageFragment

class MatchRequestsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MatchRequestsReceivedPageFragment()
            1 -> MatchRequestsSentPageFragment()
            2 -> MatchRequestsMatchedPageFragment()
            else -> error("Unknown position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Matching Provided"
            1 -> "Send"
            2 -> "Recieved"
            else -> error("Unknown position")
        }
    }

}