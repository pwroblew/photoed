package com.pwroblew.photoed.lib.impl_io

import com.pwroblew.photoed.lib.Image

import java.awt.{Dimension, Graphics}
import javax.swing.JPanel

final class EdImageJPanel(private var edImage: Image) extends JPanel {

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    edImage.draw(g)
  }

  override def getPreferredSize: Dimension =
    new Dimension(edImage.width, edImage.height)

  def replaceImage(newEdImage: Image): Unit = {
    edImage = newEdImage
    super.revalidate()
    super.repaint()
  }

  def getImage: Image = edImage
}
