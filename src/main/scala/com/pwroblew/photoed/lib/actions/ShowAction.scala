package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.{Ref, Resource}
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

class ShowAction[F[_]: MonadThrow: Console] extends EditorActionBasic[F] {

  override def actB(
      appState: Ref[F, PhotoEdAppState],
      commandDetails: List[String]
  ): F[Unit] = appState.update(state =>
    state.copy(
      isShowing = true,
      toBeShowed = true
    )
  )

  override def next: EditorActionShowable[F] = new DisplayAction[F]()
}
