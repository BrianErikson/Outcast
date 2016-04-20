package com.beariksonstudios.outcast

import com.sun.syndication.feed.synd.SyndFeedImpl
import java.net.URL

data class Podcast(val title: String, val feed: SyndFeedImpl, val imageUrl: URL?, val description: String?);