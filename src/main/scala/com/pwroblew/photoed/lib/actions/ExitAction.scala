package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState

final class ExitAction[F[_]: {MonadThrow, Console}] extends EditorActionBasic[F] {

  override def actB(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] = {

    val headCommand: String = commandDetails.head // TODO consider headOption
    headCommand match {
      case "exit"    => AdditionalActions(List("clear", "exitRaw"), List.empty[String]).pure[F]
      case "exitRaw" => stateRef.update(state =>
          state.copy(
            toBeContinued = false
          )
        )
          >> AdditionalActions.empty.pure[F]
    }

  }

  override def keywords: List[String] = List("exit", "exitRaw")
}
