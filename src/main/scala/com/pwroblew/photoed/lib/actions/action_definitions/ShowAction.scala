package com.pwroblew.photoed.lib.actions.action_definitions

import cats.MonadThrow
import cats.effect.std.Console
import cats.effect.{Ref, Resource}
import cats.syntax.all.*
import com.pwroblew.photoed.lib.actions.ActionKeyword.{DISPLAY, SHOW}
import com.pwroblew.photoed.lib.actions.{ActionKeyword, AdditionalActions, EditorActionShowable}
import com.pwroblew.photoed.lib.impl_f.WindowsManager
import com.pwroblew.photoed.lib.{ImageWindow, PhotoEdAppState}

class ShowAction[F[_]: {MonadThrow, Console}](using
    makeImageWindowResource: String => Resource[F, ImageWindow[F]]
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
    } yield AdditionalActions(List.empty[String], List(s"${DISPLAY.toCmd} $imageId"))
  }

  override def keywords: List[ActionKeyword] = List(SHOW)
}

object ShowAction {
  def apply[F[_]: {MonadThrow, Console}](using
      makeImageWindowResource: String => Resource[F, ImageWindow[F]]
  ): ShowAction[F] = new ShowAction()
}
