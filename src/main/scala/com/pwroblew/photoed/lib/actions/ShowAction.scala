package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.{Ref, Resource}
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

class ShowAction[F[_]: {MonadThrow, Console}] extends EditorActionBasic[F] {

  override def actB(
      appState: Ref[F, PhotoEdAppState],
      commandDetails: List[String]
  ): F[AdditionalActions] = appState.update(state =>
    state.copy(
      isShowing = true,
      toBeShown = true
    )
  ) >> AdditionalActions(List.empty[String], List("display")).pure[F]

  override def keywords: List[String] = List("show")
}

object ShowAction {
  def apply[F[_]: {MonadThrow, Console}]: ShowAction[F] = new ShowAction()
}
