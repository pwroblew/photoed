package com.pwroblew.photoed.lib.actions

import cats.Applicative
import cats.implicits.catsSyntaxApplicativeId
import com.pwroblew.photoed.lib.actions.transformations.EdImageTransformation
import com.pwroblew.photoed.lib.{EdImage, PhotoEdAppState}

class TransformAction[F[_]: Applicative](transformation: EdImageTransformation)
    extends EditorAction[F] {

  override def run(
      state: PhotoEdAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoEdAppState)] = {

    val newState: PhotoEdAppState = state.copy(
      stateStatus = state.stateStatus :+ s"[${transformation.description}]",
      edImage = state.edImage.map(transformation.transform)
    )
    (true, newState).pure[F]
  }

}
