package com.pwroblew.photoed.lib.impl

import cats.effect.Sync
import com.pwroblew.photoed.lib.{Image, ImageViewer}

import java.awt.{Dimension, Graphics}
import javax.swing.{JFrame, JPanel, WindowConstants}

case class JState(frame: Option[JFrame], panel: Option[ImagePanel])

class ImagePanel(private var image: Image) extends JPanel {

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

  def getImage = image
}

class ImageViewerImpl[F[_]: Sync] extends ImageViewer[F] {

  private var frame: Option[JFrame]          = None
  private var imagePanel: Option[ImagePanel] = None

  override def show(image: Image): F[Unit] =
    Sync[F].delay {
      if (frame.isEmpty) {
        // initialization
        val newFrame      = new JFrame("PhotoScala")
        val newImagePanel = new ImagePanel(image)

        newFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
        newFrame.getContentPane.add(newImagePanel)
        newFrame.pack()
        newFrame.setVisible(true)

        frame = Some(newFrame)
        imagePanel = Some(newImagePanel)
        ()
      } else {
        imagePanel.foreach(_.replaceImage(image))
        frame.foreach(_.pack())
        frame.foreach(_.setTitle("blabla"))
      }
    }
}
