package com.pwroblew.photoed.lib.actions.action_definitions

import cats.MonadThrow
import cats.data.{OptionT, StateT}
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.{Image, ImageStatus, PhotoEdAppState}
import com.pwroblew.photoed.lib.actions.ActionKeyword.DISPLAY
import com.pwroblew.photoed.lib.actions.{ActionKeyword, AdditionalActions, EditorActionShowable}
import com.pwroblew.photoed.lib.impl_f.{WindowsManager, WindowsMap}

class DisplayAction[F[_]: {MonadThrow, Console}] extends EditorActionShowable[F] {

  override def act(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String],
      windowsManager: WindowsManager[F]
  ): StateT[F, WindowsMap[F], AdditionalActions] = {

    val imageStatusF: F[ImageStatus] = for {
      maybeImageStatus <- stateRef.get
                            .map(state =>
                              commandDetails.tail.headOption match {
                                case None     => state.imagesStatuses.headOption
                                case Some(id) => state.imagesStatuses.find(_.id == id)
                              }
                            )
      _                <-
        stateRef.get.map(state => state.imagesStatuses).flatMap(statuses =>
          Console[F].println(statuses.toString + " xx " + maybeImageStatus + "ss " + commandDetails)
        )
      imageStatus      <- maybeImageStatus match {
                            case None     => new RuntimeException(
                                s"Can't show the image. The image hasn't been loaded. cmd: ${commandDetails}"
                              ).raiseError
                            case Some(im) => im.pure[F]
                          }
    } yield imageStatus

    for {
      imageStatus <- StateT.liftF(imageStatusF)
      _           <- windowsManager.display(imageStatus.id, imageStatus.image)
    } yield AdditionalActions.empty

  }

  override def keywords: List[ActionKeyword] = List(DISPLAY)
}

object DisplayAction {
  def apply[F[_]: {MonadThrow, Console}]: DisplayAction[F] = new DisplayAction[F]()
}
