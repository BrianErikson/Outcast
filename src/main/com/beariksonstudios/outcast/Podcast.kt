package com.beariksonstudios.outcast

import com.sun.syndication.feed.synd.SyndFeedImpl
import podcastmanager.Feed
import java.net.URL

data class Podcast(val feed: Feed, val rss: SyndFeedImpl, val imageUrl: URL?, val description: String?);