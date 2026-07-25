package com.pwroblew.photoed.lib.actions.action_definitions

import cats.MonadThrow
import cats.data.StateT
import cats.effect.std.{Console, Dispatcher}
import cats.effect.{Ref, Resource}
import cats.syntax.all.*
import com.pwroblew.photoed.StatefulCLI.MakeImageWindowResource
import com.pwroblew.photoed.lib.actions.ActionKeyword.{DISPLAY, SHOW}
import com.pwroblew.photoed.lib.actions.{ActionKeyword, AdditionalActions, EditorActionShowable}
import com.pwroblew.photoed.lib.impl_f.{WindowsManager, WindowsMap}
import com.pwroblew.photoed.lib.{ImageStatus, ImageWindow, PhotoEdAppState}

class ShowAction[F[_]: {MonadThrow, Console}](using
    makeImageWindowResource: MakeImageWindowResource[F]
) extends EditorActionShowable[F] {

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
      imageStatus      <- maybeImageStatus match {
                            case None     => new RuntimeException(
                                s"Can't show the image. The image hasn't been loaded. cmd: ${commandDetails}"
                              ).raiseError
                            case Some(im) => im.pure[F]
                          }
    } yield imageStatus

    for {
      imageStatus <- StateT.liftF(imageStatusF)
      _           <- windowsManager.open(imageStatus.id, makeImageWindowResource)
      _           <- windowsManager.display(imageStatus.id, imageStatus.image)
    } yield AdditionalActions(
      List.empty[String],
      List(s"${DISPLAY.toCmd} ${commandDetails.tail.headOption.getOrElse("")}")
    )

  }

  override def keywords: List[ActionKeyword] = List(SHOW)

  override protected def help: StateT[F, WindowsMap[F], AdditionalActions] =
    StateT.liftF(
      Console[F].println("show: shows the image(s) window.")
        >> Console[F].println("syntax: show // applies to the first image on the status list")
        >> Console[F].println("syntax: show <id>  // applies to an image identified by 'id'")
        >> AdditionalActions.empty.pure[F]
    )
}

object ShowAction {
  def apply[F[_]: {MonadThrow, Console}](using
      makeImageWindowResource: MakeImageWindowResource[F]
  ): ShowAction[F] = new ShowAction()
}
