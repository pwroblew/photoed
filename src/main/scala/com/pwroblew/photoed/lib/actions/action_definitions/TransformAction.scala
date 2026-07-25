package com.pwroblew.photoed.lib.actions.action_definitions

import cats.MonadThrow
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState
import com.pwroblew.photoed.lib.actions.ActionKeyword.DISPLAY
import com.pwroblew.photoed.lib.actions.action_definitions.transformations.EdImageTransformation
import com.pwroblew.photoed.lib.actions.{ActionKeyword, AdditionalActions, EditorActionBasic}

class TransformAction[F[_]: {MonadThrow, Console}](transformation: EdImageTransformation)
    extends EditorActionBasic[F] {

  override def actB(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] = {

    val maybeId: Option[String] = commandDetails.tail.headOption

    for {
      imageId <- stateRef.modify { state =>
                   val imgId: String = maybeId.getOrElse(state.imagesStatuses.head.id)

                   val newState: PhotoEdAppState[F] = state.copy(
                     imagesStatuses = state.imagesStatuses.map(status =>
                       if status.id == imgId then
                         status.copy(image = transformation.transform(status.image))
                       else status
                     )
                   )
                   (newState, imgId)
                 }
    } yield AdditionalActions(List.empty[String], List(s"${DISPLAY.toCmd} ${imageId}"))
  }

  override def keywords: List[ActionKeyword] = transformation.keywords

  override protected def helpB: F[AdditionalActions] =
    Console[F].println(transformation.help) >> AdditionalActions.empty.pure
}
