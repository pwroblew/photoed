package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.syntax.all.*
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

class ClearAction[F[_]: MonadThrow](imageViewer: EdImageViewer[F]) extends EditorAction[F] {

  override def act(
      state: PhotoEdAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoEdAppState)] = {

    (true, PhotoEdAppState.initialState).pure[F]
  }

  override def prev: EditorAction[F] = new CloseAction[F](imageViewer)
}
