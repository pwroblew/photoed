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
  ): F[AdditionalActions] = {

    val maybeId: Option[String] = commandDetails.tail.headOption

    for {
      imageId <- stateRef.modify { state =>
                   val imgId: String = maybeId.getOrElse(state.imagesStatus.head.id)

                   val newState: PhotoEdAppState[F] = state.copy(
                     imagesStatus = state.imagesStatus.map(status =>
                       if status.id == imgId then
                         status.copy(image = transformation.transform(status.image))
                       else status
                     )
                   )
                   (newState, imgId)
                 }
    } yield AdditionalActions(List.empty[String], List(s"display ${imageId}"))
  }

  override def keywords: List[String] = transformation.keywords
}
