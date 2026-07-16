package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.{Ref, Resource}
import cats.effect.std.Console
import cats.syntax.all._
import cats.implicits.catsSyntaxFlatMapOps
import com.pwroblew.photoed.lib.actions.transformations.EdImageTransformation
import com.pwroblew.photoed.lib.{EdImage, EdImageViewer, PhotoEdAppState}

class TransformAction[F[_]: {MonadThrow,
  Console}](transformation: EdImageTransformation)
    extends EditorActionBasic[F] {

  override def actB(
      state: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] = state.update { st =>
    st.copy(
      edImage = st.edImage.map(transformation.transform),
      toBeContinued = true
    )
  } >> AdditionalActions(List.empty[String], List("display")).pure[F]

  override def keywords: List[String] = transformation.keywords
}
