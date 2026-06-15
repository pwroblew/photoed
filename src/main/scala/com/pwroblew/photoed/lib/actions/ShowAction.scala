package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.implicits.catsSyntaxApplicativeId
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

class ShowAction[F[_]: MonadThrow](imageViewer: EdImageViewer[F]) extends EditorAction[F] {

  override def act(
      state: PhotoEdAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoEdAppState)] =

    (state.isShowing, state.swingComponents) match {
      case (true, Some(_)) => (true, state).pure[F]
      case (false, _)      => (true, state.copy(isShowing = true)).pure[F]
      case (true, None)    =>
        MonadThrow[F].raiseError(new RuntimeException("invalid application state"))
    }

  override def next: EditorAction[F] = new DisplayAction[F](imageViewer)
}
