package com.pwroblew.photoed.lib

import java.awt.Graphics
import java.awt.image.BufferedImage

class EdImage private (val buffImage: BufferedImage) {
  def getImage: BufferedImage = buffImage
  val width: Int              = buffImage.getWidth
  val height: Int             = buffImage.getHeight

  def draw(g: Graphics): Unit =
    g.drawImage(buffImage, 0, 0, null)
}

object EdImage {
  def empty: EdImage                                  = new EdImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB))
  def fromBuffered(buffImage: BufferedImage): EdImage = new EdImage(buffImage)
}
