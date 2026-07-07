package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.{Ref, Resource}
import cats.effect.std.Console
import com.pwroblew.photoed.lib.actions.transformations.EdImageTransformation
import com.pwroblew.photoed.lib.{EdImage, EdImageViewer, PhotoEdAppState}

class TransformAction[F[_]: {MonadThrow,
  Console}](transformation: EdImageTransformation)
    extends EditorActionBasic[F] {

  override def actB(
      state: Ref[F, PhotoEdAppState],
      commandDetails: List[String]
  ): F[Unit] = state.update { st =>
    st.copy(
      history = st.history :+ s"[${transformation.description}]",
      edImage = st.edImage.map(transformation.transform),
      toBeContinued = true
    )
  }

  override def next: EditorActionShowable[F] = new DisplayAction[F]()

}
