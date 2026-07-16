package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.{Ref, Resource}
import cats.effect.std.Console
import cats.syntax.all._
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

final class ExitAction[F[_]: {MonadThrow, Console}] extends EditorActionBasic[F] {
  override def actB(
      stateRef: Ref[F, PhotoEdAppState],
      commandDetails: List[String]
  ): F[AdditionalActions] = stateRef.update(state =>
    state.copy(
      history = state.history :+ "[exiting]",
      toBeContinued = false
    )
  ) >> AdditionalActions(List("clear"), List.empty[String]).pure[F]

  override def keywords: List[String] = List("exit")
}
