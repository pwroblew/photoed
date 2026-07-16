package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.actions.SaveAction.saveImage
import com.pwroblew.photoed.lib.{EdImage, EdImageFiles, PhotoEdAppState}

class SaveResAction[F[_]: {MonadThrow, Console}](imageLoader: EdImageFiles[F])
    extends EditorActionBasic[F] {

  override def actB(
      state: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] = {
    val maybePath: Option[String] = commandDetails.drop(1).headOption.map("src/main/resources/" + _)
    saveImage(imageLoader.save)(state, maybePath) >> AdditionalActions.empty.pure[F]
  }

  override def keywords: List[String] = List("save-res")
}

object SaveResAction {
  def apply[F[_]: {MonadThrow, Console}](using imageLoader: EdImageFiles[F]): SaveResAction[F] =
    new SaveResAction(imageLoader)
}
