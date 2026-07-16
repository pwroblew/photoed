package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.{Ref, Resource}
import cats.effect.std.Console
import cats.syntax.all._
import com.pwroblew.photoed.lib.actions.LoadAction.loadImage
import com.pwroblew.photoed.lib.{EdImageFiles, EdImageViewer, PhotoEdAppState}

class LoadResAction[F[_]: {MonadThrow, Console}](
    imageLoader: EdImageFiles[F]
) extends EditorActionBasic[F] {

  override def actB(
      state: Ref[F, PhotoEdAppState],
      commandDetails: List[String]
  ): F[AdditionalActions] = {
    val path: Option[String] = commandDetails.drop(1).headOption.map("src/main/resources/" + _)
    loadImage(imageLoader.load)(path)(state) >> AdditionalActions(
      List.empty[String],
      List("show")
    ).pure[F]
  }

  override def keywords: List[String] = List("load-res")
}

object LoadResAction {
  def apply[F[_]: {MonadThrow, Console}](using imageLoader: EdImageFiles[F]): LoadResAction[F] =
    new LoadResAction(imageLoader)

}
