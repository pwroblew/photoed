package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.std.Console
import cats.implicits.catsSyntaxApplicativeId
import com.pwroblew.photoed.lib.actions.transformations.EdImageTransformation
import com.pwroblew.photoed.lib.{EdImage, EdImageViewer, PhotoEdAppState}

class TransformAction[F[_]: {MonadThrow,
  Console}](transformation: EdImageTransformation, imageViewer: EdImageViewer[F])
    extends EditorAction[F] {

  override def act(
      state: PhotoEdAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoEdAppState)] = {

    val newState: PhotoEdAppState = state.copy(
      stateStatus = state.stateStatus :+ s"[${transformation.description}]",
      edImage = state.edImage.map(transformation.transform)
    )
    (true, newState).pure[F]
  }

  override def next: EditorAction[F] = new DisplayAction[F](imageViewer)

}
