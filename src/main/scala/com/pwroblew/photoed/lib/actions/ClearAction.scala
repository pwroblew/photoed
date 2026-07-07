package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.{Ref, Resource}
import cats.effect.std.Console
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

class ClearAction[F[_]: MonadThrow: Console] extends EditorActionBasic[F] {

  override def actB(
      state: Ref[F, PhotoEdAppState],
      commandDetails: List[String]
  ): F[Unit] = state.set(PhotoEdAppState.initialState)

  override def prevB: EditorActionBasic[F] = new CloseAction[F]()
}
