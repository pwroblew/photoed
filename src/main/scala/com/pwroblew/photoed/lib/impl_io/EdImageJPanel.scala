package com.pwroblew.photoed.lib.impl_io

import com.pwroblew.photoed.lib.EdImage

import java.awt.{Dimension, Graphics}
import javax.swing.JPanel

final class EdImageJPanel(private var edImage: EdImage) extends JPanel {

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    edImage.draw(g)
  }

  override def getPreferredSize: Dimension =
    new Dimension(edImage.width, edImage.height)

  def replaceImage(newEdImage: EdImage): Unit = {
    edImage = newEdImage
    super.revalidate()
    super.repaint()
  }

  def getImage: EdImage = edImage
}
