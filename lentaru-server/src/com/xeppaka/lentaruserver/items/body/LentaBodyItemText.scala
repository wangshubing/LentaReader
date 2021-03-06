package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.ItemBase
import com.xeppaka.lentaruserver.CDataEscaper

/**
 * Created with IntelliJ IDEA.
 * User: nnm
 * Date: 9/23/13
 * Time: 10:09 PM
 * To change this template use File | Settings | File Templates.
 */

case class LentaBodyItemText(val text: String) extends ItemBase {
  override def toXml(indent: String): String = {
    val escaped = CDataEscaper.escapeText(text)
    return s"$indent<text>\n$escaped\n$indent</text>\n"
  }

  def merge(other: LentaBodyItemText): LentaBodyItemText = {
    LentaBodyItemText(text + other.text)
  }

  def merge(other: String): LentaBodyItemText = {
    LentaBodyItemText(text + other)
  }

  override def hashCode(): Int = {
    var hash = 1
    hash = hash * 31 + text.hashCode

    hash
  }

  override def equals(obj: scala.Any): Boolean = {
    if (!obj.isInstanceOf[LentaBodyItemText])
      false
    else {
      val other = obj.asInstanceOf[LentaBodyItemText]
      text == other.text
    }
  }
}
