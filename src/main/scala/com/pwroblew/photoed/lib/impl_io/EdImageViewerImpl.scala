package com.pwroblew.photoed.lib.impl_io

import cats.data.OptionT
import cats.effect.{IO, Ref, Resource}
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxOptionId}
import com.pwroblew.photoed.lib.impl_io.EdImageJPanel
import com.pwroblew.photoed.lib.impl_io.EdImageViewerImpl.{makeResource, onEDT}
import com.pwroblew.photoed.lib.{EdImage, EdImageViewer, PhotoEdAppState}

import javax.swing.{JFrame, WindowConstants}

class EdImageViewerImpl(val name: String, val jFrame: JFrame, val imageJPanel: EdImageJPanel)
    extends EdImageViewer[IO] {

  override def show(appState: Ref[IO, PhotoEdAppState])(edImage: EdImage): IO[Unit] = {

    for {
      isShowing <- appState.get.map(_.isShowing)
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

  override def close(appState: Ref[IO, PhotoEdAppState]): IO[Unit] = {
    for {
      _ <- onEDT {
             jFrame.dispose()
           }
      _ <- appState.update(_.copy(isShowing = false, toBeShowed = false))
    } yield ()
  }

  override def hide(appState: Ref[IO, PhotoEdAppState]): IO[Unit] =
    for {
      _ <- onEDT {
             jFrame.setVisible(false)
           }
      _ <- appState.update(_.copy(isShowing = false))
    } yield ()

}

object EdImageViewerImpl {

  def makeResource(name: String): Resource[IO, EdImageViewer[IO]] = {
    Resource.make {
      onEDT {
        val jFrame      = new JFrame(name)
        val imageJPanel = new EdImageJPanel(EdImage.empty)

        jFrame.add(imageJPanel)
        jFrame.pack()
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

        (jFrame, imageJPanel)
      }.map { (jFrame, jPanel) => new EdImageViewerImpl(name, jFrame, jPanel) }
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
