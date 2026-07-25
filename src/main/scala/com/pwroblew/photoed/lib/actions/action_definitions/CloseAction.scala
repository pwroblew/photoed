package com.pwroblew.photoed.lib.actions.action_definitions

import cats.MonadThrow
import cats.data.{NonEmptyList, StateT}
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.actions.ActionKeyword.CLOSE
import com.pwroblew.photoed.lib.actions.{ActionKeyword, AdditionalActions, EditorActionShowable}
import com.pwroblew.photoed.lib.impl_f.{WindowsManager, WindowsMap}
import com.pwroblew.photoed.lib.{ImageStatus, PhotoEdAppState}

class CloseAction[F[_]: {MonadThrow, Console}] extends EditorActionShowable[F] {

  override def act(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String],
      windowsManager: WindowsManager[F]
  ): StateT[F, WindowsMap[F], AdditionalActions] = {

    val imageStatusF: F[NonEmptyList[ImageStatus]] = for {
      imageStatusList <- stateRef.get
                           .map(state =>
                             maybeImageId(commandDetails) match {
                               case None                    => state.imagesStatuses.headOption.toList
                               case Some(id) if id != "ALL" =>
                                 state.imagesStatuses.find(_.id == id).toList
                               case _                       => state.imagesStatuses
                             }
                           )
      imageStatusNel  <- imageStatusList match {
                           case Nil           => new RuntimeException(
                               s"Can't show the image. The image hasn't been loaded. cmd: ${commandDetails}"
                             ).raiseError
                           case head_ :: tail => NonEmptyList(head_, tail).pure[F]
                         }
    } yield imageStatusNel

    for {
      imageStatusNel <- StateT.liftF(imageStatusF)
      _              <- imageStatusNel.traverse(imageStatus => windowsManager.close(imageStatus.id))
    } yield AdditionalActions.empty
  }

  override def keywords: List[ActionKeyword] = List(CLOSE)

  override protected def help: StateT[F, WindowsMap[F], AdditionalActions] =
    StateT.liftF(
      Console[F].println("close: closes the image(s) window.")
        >> Console[F].println("syntax: close ALL  // applies to ALL images")
        >> Console[F].println("syntax: close <id>  // applies to an image identified by 'id'")
        >> AdditionalActions.empty.pure[F]
    )
}

object CloseAction {
  def apply[F[_]: {MonadThrow, Console}]: CloseAction[F] = new CloseAction[F]()
}
