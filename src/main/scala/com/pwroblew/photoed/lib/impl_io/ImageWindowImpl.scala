package com.pwroblew.photoed.lib.impl_io

import cats.data.OptionT
import cats.effect.{IO, Ref, Resource}
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxOptionId}
import com.pwroblew.photoed.lib.impl_io.EdImageJPanel
import com.pwroblew.photoed.lib.impl_io.ImageWindowImpl.{makeResource, onEDT}
import com.pwroblew.photoed.lib.{Image, ImageWindow, PhotoEdAppState}

import javax.swing.{JFrame, WindowConstants}

class ImageWindowImpl(val name: String, val jFrame: JFrame, val imageJPanel: EdImageJPanel)
    extends ImageWindow[IO] {

  override def show(appState: Ref[IO, PhotoEdAppState[IO]])(edImage: Image): IO[Unit] = {

    for {
      isShowing <- appState.get.map(_.imagesStatus.head.isShowing)
      _         <- if !isShowing then IO.unit
      else {
        for {
          _ <- onEDT {
                 imageJPanel.replaceImage(edImage)
                 imageJPanel.repaint()
                 jFrame.pack()
                 jFrame.setVisible(true)
               }

        } yield ()
      }
    } yield ()

  }

  override def close(appState: Ref[IO, PhotoEdAppState[IO]]): IO[Unit] = {
    for {
      _ <- onEDT {
             jFrame.dispose()
           }
      _ <- appState.update(state =>
             state.copy(imagesStatus =
               state.imagesStatus.map(status => status.copy(isShowing = false, toBeShown = false))
             )
           )
    } yield ()
  }

  override def hide(appState: Ref[IO, PhotoEdAppState[IO]]): IO[Unit] =
    for {
      _ <- onEDT {
             jFrame.setVisible(false)
           }
      _ <- appState.update(state => state.copy(imagesStatus = state.imagesStatus.map(status => status.copy(isShowing = false))))
    } yield ()

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
    } { viewer =>
      onEDT {
        viewer.jFrame.dispose()
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
