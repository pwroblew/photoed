package com.pwroblew.photoed.lib.impl_io

import com.pwroblew.photoed.lib.Image

import java.awt.{Dimension, Graphics}
import javax.swing.JPanel

final class ImageJPanel(private var image: Image) extends JPanel {

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    image.draw(g)
  }

  override def getPreferredSize: Dimension =
    new Dimension(image.width, image.height)

  def replaceImage(newImage: Image): Unit = {
    image = newImage
    super.revalidate()
    super.repaint()
  }

  def getImage: Image = image
}
