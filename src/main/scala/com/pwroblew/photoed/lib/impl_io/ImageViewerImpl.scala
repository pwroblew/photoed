package com.pwroblew.photoed.lib.impl_io

import cats.effect.IO
import com.pwroblew.photoed.lib.impl_io.ImageJPanel
import com.pwroblew.photoed.lib.impl_io.ImageViewerImpl.onEDT
import com.pwroblew.photoed.lib.{Image, ImageViewer}

import javax.swing.{JFrame, WindowConstants}

final class ImageViewerImpl private (
    private val jFrame: JFrame,
    private val imageJPanel: ImageJPanel
) extends ImageViewer[IO] {

  override def show(image: Image): IO[Unit] = {
    onEDT {
      imageJPanel.replaceImage(image)
      imageJPanel.repaint()
      jFrame.pack()
      jFrame.setVisible(true)
    }
  }

  /*
  override def close: IO[Unit] =
    onEDT {
      frame.dispose()
    }
   */
}

object ImageViewerImpl {

  // running Swing's stuff in the Event Dispatch Thread
  // specifically: take the Swing specific code as `body`
  // and run it (asynchronously) in EDT, and return the result as `IO[A]`.
  def onEDT[A](body: => A): IO[A] = {
    IO.async_[A] { cb =>
      javax.swing.SwingUtilities.invokeLater { () =>
        try cb(Right(body))
        catch {
          case t: Throwable => cb(Left(t))
        }
      }
    }
  }

  def create(name: String): ImageViewerImpl = {
    val jFrame      = new JFrame(name)
    val imageJPanel = new ImageJPanel(Image.empty)
    jFrame.add(imageJPanel)
    jFrame.pack()
    jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

    new ImageViewerImpl(jFrame, imageJPanel)
  }

}
