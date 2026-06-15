package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.implicits.catsSyntaxApplicativeId
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

final class ExitAction[F[_]: MonadThrow](imageViewer: EdImageViewer[F]) extends EditorAction[F] {
  override def act(
      state: PhotoEdAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoEdAppState)] = {

    val newState: PhotoEdAppState = state.copy(
      stateStatus = state.stateStatus :+ "[exiting]"
    )
    (false, newState).pure[F]

  }

  override def prev: EditorAction[F] = new ClearAction[F](imageViewer)
}
