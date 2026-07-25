package com.pwroblew.photoed.lib.actions.action_definitions

import cats.MonadThrow
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState
import com.pwroblew.photoed.lib.actions.ActionKeyword.{CLEAR, CLEAR_RAW, CLOSE}
import com.pwroblew.photoed.lib.actions.{ActionKeyword, AdditionalActions, EditorActionBasic}

class ClearAction[F[_]: {MonadThrow, Console}] extends EditorActionBasic[F] {

  override def actB(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] = {

    val maybeMainAction: Option[ActionKeyword] = commandDetails.headOption.flatMap(ActionKeyword.fromCmd)
    val maybeImageId: Option[String]           = commandDetails.tail.headOption

    (maybeMainAction, maybeImageId) match {

      case (Some(CLEAR), None) =>
        AdditionalActions(List(s"${CLOSE.toCmd} ALL", s"${CLEAR_RAW.toCmd}"), List.empty).pure[F]

      case (Some(CLEAR), Some(id)) =>
        AdditionalActions(List(s"${CLOSE.toCmd} $id", s"${CLEAR_RAW.toCmd} $id"), List.empty).pure[F]

      case (Some(CLEAR_RAW), None) => stateRef.update(state => state.copy(imagesStatuses = List.empty))
          >> AdditionalActions.empty.pure[F]

      case (Some(CLEAR_RAW), Some(id)) => stateRef.update(state =>
          state.copy(
            imagesStatuses = state.imagesStatuses.filterNot(_.id == id)
          )
        )
          >> AdditionalActions.empty.pure[F]

      case _ => new RuntimeException("FATAL ERROR").raiseError
    }
  }

  override def keywords: List[ActionKeyword] = List(CLEAR, CLEAR_RAW)

  override protected def helpB: F[AdditionalActions] =
    Console[F].println("clear: unloads the image(s) from memory and closes any open window associated with it.")
      >> Console[F].println("syntax: clear  // applies to ALL images")
      >> Console[F].println("syntax: clear <id>  // applies to an image identified by 'id'")
      >> AdditionalActions.empty.pure
}

object ClearAction {
  def apply[F[_]: {MonadThrow, Console}]: ClearAction[F] = new ClearAction[F]()
}
