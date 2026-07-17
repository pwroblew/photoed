package com.pwroblew.photoed.lib.actions.action_definitions

import cats.MonadThrow
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState
import com.pwroblew.photoed.lib.actions.ActionKeyword.{CLEAR, EXIT, EXIT_RAW}
import com.pwroblew.photoed.lib.actions.{ActionKeyword, AdditionalActions, EditorActionBasic}

final class ExitAction[F[_]: {MonadThrow, Console}] extends EditorActionBasic[F] {

  override def actB(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] = {

    commandDetails.headOption.flatMap(ActionKeyword.fromCmd) match {
      case Some(EXIT)     => AdditionalActions(List(CLEAR.toCmd, EXIT_RAW.toCmd), List.empty).pure[F]
      case Some(EXIT_RAW) => stateRef.update(state =>
          state.copy(
            toBeContinued = false
          )
        )
          >> AdditionalActions.empty.pure[F]
      case _              => new RuntimeException("FATAL ERROR in ExitAction").raiseError
    }

  }

  override def keywords: List[ActionKeyword] = List(EXIT, EXIT_RAW)
}
