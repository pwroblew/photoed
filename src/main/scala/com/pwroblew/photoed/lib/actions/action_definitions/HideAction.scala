package com.pwroblew.photoed.lib.actions.action_definitions

import cats.MonadThrow
import cats.data.{OptionT, StateT}
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.actions.ActionKeyword.HIDE
import com.pwroblew.photoed.lib.actions.{ActionKeyword, AdditionalActions, EditorActionShowable}
import com.pwroblew.photoed.lib.impl_f.{WindowsManager, WindowsMap}
import com.pwroblew.photoed.lib.{ImageStatus, PhotoEdAppState}

class HideAction[F[_]: {MonadThrow, Console}] extends EditorActionShowable[F] {

  override def act(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String],
      windowsManager: WindowsManager[F]
  ): StateT[F, WindowsMap[F], AdditionalActions] = {

    val maybeId: Option[String] = commandDetails.tail.headOption

    val imageStatusF: F[ImageStatus] = for {
      maybeImage <- stateRef.get
                      .map(state =>
                        maybeId match {
                          case None     => state.imagesStatuses.headOption
                          case Some(id) => state.imagesStatuses.find(_.id == id)
                        }
                      )
      image      <- maybeImage match {
                      case None     => new RuntimeException(
                          s"Can't show the image. The image hasn't been loaded. cmd: ${commandDetails}"
                        ).raiseError
                      case Some(im) => im.pure[F]
                    }
    } yield image

    for {
      imageStatus <- StateT.liftF(imageStatusF)
      _           <- windowsManager.hide(imageStatus.id)
    } yield AdditionalActions.empty
  }

  override def keywords: List[ActionKeyword] = List(HIDE)
}

object HideAction {
  def apply[F[_]: {MonadThrow, Console}]: HideAction[F] = new HideAction()
}
