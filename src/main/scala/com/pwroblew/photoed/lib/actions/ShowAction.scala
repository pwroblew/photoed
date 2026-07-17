package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.{IO, Ref, Resource}
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.impl_f.{ViewerWindow, WindowsManager}
import com.pwroblew.photoed.lib.impl_io.EdImageViewerImpl
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

class ShowAction[F[_]: {MonadThrow, Console}](using
    makeImageWindowResource: String => Resource[F, EdImageViewer[F]]
) extends EditorActionShowable[F] {

  override def act(
      appState: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String],
      windowsManager: WindowsManager[F]
  ): F[AdditionalActions] = {

    val maybeId: Option[String] = commandDetails.tail.headOption

    for {
      maybeImageId <- appState.get.map { state =>
                        val maybeString: Option[String] = maybeId match {
                          case None     => state.imagesStatus.headOption.map(_.id)
                          case Some(id) => state.imagesStatus.find(_.id == id).map(_.id)
                        }
                        maybeString
                      }
      imageId      <- maybeImageId match {
                        case None    =>
                          new RuntimeException("invalid image id").raiseError[F, String]
                        case Some(x) => x.pure[F]
                      }
      _            <- appState.update(state =>
                        state.copy(
                          imagesStatus =
                            state.imagesStatus.map(status =>
                              if (status.id == imageId) then status.copy(isShowing = true, toBeShown = true)
                              else status
                            )
                        )
                      )
      _            <- windowsManager.open(imageId, makeImageWindowResource(imageId))
    } yield AdditionalActions(List.empty[String], List(s"display $imageId"))
  }

  override def keywords: List[String] = List("show")
}

object ShowAction {
  def apply[F[_]: {MonadThrow, Console}](using
      makeImageWindowResource: String => Resource[F, EdImageViewer[F]]
  ): ShowAction[F] = new ShowAction()
}
