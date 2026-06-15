package com.pwroblew.photoed.lib.impl_io

import cats.data.OptionT
import cats.effect.IO
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxOptionId}
import com.pwroblew.photoed.lib.impl_io.EdImageJPanel
import com.pwroblew.photoed.lib.{EdImage, EdImageViewer, PhotoEdAppState}

import javax.swing.{JFrame, WindowConstants}

object EdImageViewerImpl extends EdImageViewer[IO] {

  override def show(appState: PhotoEdAppState)(edImage: EdImage): IO[PhotoEdAppState] = {
    if !appState.isShowing then IO.pure(appState)
    else {
      val swingUpdated: OptionT[IO, (JFrame, EdImageJPanel)] = for {
        (jFrame, jPanel) <- appState.swingComponents match {
                              case None    => OptionT.liftF(create("photoed"))
                              case Some(_) => OptionT.fromOption[IO](appState.swingComponents)
                            }
        _                <- OptionT.liftF {
                              onEDT {
                                jPanel.replaceImage(edImage)
                                jPanel.repaint()
                                jFrame.pack()
                                jFrame.setVisible(true)
                              }
                            }
      } yield (jFrame, jPanel)

      swingUpdated
        .getOrRaise(new RuntimeException("Couldn't show"))
        .map(swingStuff => appState.copy(swingComponents = swingStuff.some))
    }
  }

  override def close(appState: PhotoEdAppState): IO[PhotoEdAppState] = {
    if appState.swingComponents.isEmpty then appState.pure[IO]
    else {
      val res: OptionT[IO, PhotoEdAppState] = for {
        (jFrame, jPanel) <- OptionT.fromOption[IO](appState.swingComponents)
        _                <- OptionT.liftF {
                              onEDT {
                                jFrame.dispose()
                              }
                            }
      } yield appState.copy(isShowing = false, swingComponents = None)

      res.getOrRaise(new RuntimeException("Couldn't close the window."))
    }
  }

  override def hide(appState: PhotoEdAppState): IO[PhotoEdAppState] = {
    if appState.swingComponents.isEmpty then appState.pure[IO]
    else {
      val res: OptionT[IO, PhotoEdAppState] = for {
        (jFrame, jPanel) <- OptionT.fromOption[IO](appState.swingComponents)
        _                <- OptionT.liftF {
                              onEDT {
                                jFrame.setVisible(false)
                              }
                            }
      } yield appState.copy(isShowing = false)

      res.getOrRaise(new RuntimeException("Couldn't close the window."))
    }
  }

  def create(name: String): IO[(jFrame: JFrame, imageJPanel: EdImageJPanel)] = {
    onEDT {
      val jFrame      = new JFrame(name)
      val imageJPanel = new EdImageJPanel(EdImage.empty)

      jFrame.add(imageJPanel)
      jFrame.pack()
      jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

      (jFrame, imageJPanel)
    }
  }

  // running Swing's stuff in the Event Dispatch Thread
  // specifically: take the Swing specific code as `body`
  // and run it (asynchronously) in EDT, and return the result as `IO[A]`.
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
