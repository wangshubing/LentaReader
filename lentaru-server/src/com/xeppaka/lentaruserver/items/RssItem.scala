package com.xeppaka.lentaruserver.items

import com.xeppaka.lentaruserver.NewsType.NewsType
import com.xeppaka.lentaruserver.Rubrics.Rubrics
import scala.xml.Node
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created with IntelliJ IDEA.
 * User: kacpa01
 * Date: 10/23/13
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
class RssItem(
  val guid: String,
  val title: String,
  val author: String,
  val link: String,
  val image: String,
  val description: String,
  val pubDate: Long)
{
  override def toString() = s"RssItem[guid=$guid, title=$title, author=$author, link=$link, image=$image, description=$description, pubDate=$pubDate]"
}

object RssItem {
  val rssDatePattern = "EEE, dd MMM yyyy HH:mm:ss Z"
  val dateFormat = new SimpleDateFormat(rssDatePattern, Locale.US)

  def apply(rssnode: Node): RssItem = {
    val guid = (rssnode \\ "guid").text.trim
    val title = (rssnode \\ "title").text.trim
    val author = (rssnode \\ "author").text.trim
    val link = (rssnode \\ "link").text.trim
    val image = (rssnode \\ "enclosure/@url").text.trim
    val description = (rssnode \\ "description").text.trim
    val pubDate = dateFormat.parse((rssnode \\ "pubDate").text)

    new RssItem(guid, title, author, link, image, description, pubDate.getTime)
  }
}