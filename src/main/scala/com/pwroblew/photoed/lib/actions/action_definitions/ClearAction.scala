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

    commandDetails.headOption.flatMap(ActionKeyword.fromCmd) match {
      case Some(CLEAR)     => AdditionalActions(List(s"${CLOSE.toCmd} ALL", CLEAR_RAW.toCmd), List.empty).pure[F]
      case Some(CLEAR_RAW) => stateRef.update(state => state.copy(imagesStatuses = List.empty))
          >> AdditionalActions.empty.pure[F]
      case _               => new RuntimeException("FATAL ERROR").raiseError
    }
  }

  override def keywords: List[ActionKeyword] = List(CLEAR, CLEAR_RAW)
}

object ClearAction {
  def apply[F[_]: {MonadThrow, Console}]: ClearAction[F] = new ClearAction[F]()
}
