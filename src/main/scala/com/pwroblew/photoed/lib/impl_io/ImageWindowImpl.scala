package com.pwroblew.photoed.lib.impl_io

import cats.effect.{IO, Ref, Resource}
import com.pwroblew.photoed.lib.impl_io.EdImageJPanel
import com.pwroblew.photoed.lib.impl_io.ImageWindowImpl.onEDT
import com.pwroblew.photoed.lib.{Image, ImageWindow, PhotoEdAppState}

import javax.swing.{JFrame, WindowConstants}

class ImageWindowImpl(val name: String, val jFrame: JFrame, val imageJPanel: EdImageJPanel)
    extends ImageWindow[IO] {

  override def display(edImage: Image): IO[Unit] = {

    for {
      _ <- onEDT {
             imageJPanel.replaceImage(edImage)
             imageJPanel.repaint()
             jFrame.pack()
             jFrame.setVisible(true)
           }
    } yield ()

  }

  override def close: IO[Unit] = {
    for {
      _ <- onEDT {
             jFrame.dispose()
           }

    } yield ()
  }

  override def hide: IO[Unit] =
    for {
      _ <- onEDT {
             jFrame.setVisible(false)
           }

    } yield ()

  override def isBeingShown: IO[Boolean] = {
    for {
      isBeingShown <- onEDT {
                        jFrame.isVisible
                      }
    } yield isBeingShown
  }
}

object ImageWindowImpl {

  def makeResource(name: String): Resource[IO, ImageWindow[IO]] = {
    Resource.make {
      onEDT {
        val jFrame      = new JFrame(name)
        val imageJPanel = new EdImageJPanel(Image.empty)

        jFrame.add(imageJPanel)
        jFrame.pack()
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

        (jFrame, imageJPanel)
      }.map { (jFrame, jPanel) => new ImageWindowImpl(name, jFrame, jPanel) }
    } { imageWindow =>
      onEDT {
        imageWindow.jFrame.dispose()
      }
    }

  }

  // running Swing's stuff on the Event Dispatch Thread
  private def onEDT[A](body: => A): IO[A] = {
    IO.async_[A] { cb =>
      javax.swing.SwingUtilities.invokeLater { () =>
        try cb(Right(body))
        catch {
          case t: Throwable => cb(Left(t))
        }
      }
    }
  }

}
