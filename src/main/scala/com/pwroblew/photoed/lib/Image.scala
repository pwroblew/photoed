package com.pwroblew.photoed.lib

import cats.effect.IO

import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Image /*private*/ (private val buffImage: BufferedImage) {
  def save: Unit = ???
  def show: Unit = ???

  def getImage: BufferedImage = buffImage
  val width: Int              = buffImage.getWidth
  val height: Int             = buffImage.getHeight

  def draw(g: Graphics): Unit =
    g.drawImage(buffImage, 0, 0, null)
}

object Image {
  def load(path: String): IO[Image] =
    IO.blocking(ImageIO.read(new File(path)))
      .map(new Image(_))
}
