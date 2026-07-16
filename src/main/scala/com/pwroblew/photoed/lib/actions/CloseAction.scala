package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.{Ref, Resource}
import cats.syntax.all.*
import cats.effect.std.Console
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

class CloseAction[F[_]: {MonadThrow, Console}] extends EditorActionBasic[F] {

  override def actB(
      state: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] =
    state.update(_.copy(toBeShown = false)) >> AdditionalActions.empty.pure[F]

  override def keywords: List[String] = List("close")
}

object CloseAction {
  def apply[F[_]: {MonadThrow, Console}]: CloseAction[F] = new CloseAction[F]()
}
