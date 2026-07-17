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
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] = stateRef.update { state =>
    state.copy(
      imagesStatus = state.imagesStatus.map(status =>
        status.copy(image = transformation.transform(status.image))
      )
    )
  } >> AdditionalActions(List.empty[String], List("display")).pure[F]

  override def keywords: List[String] = transformation.keywords
}
