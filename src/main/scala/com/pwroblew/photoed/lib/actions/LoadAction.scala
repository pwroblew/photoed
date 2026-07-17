package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.actions.LoadAction.loadImage
import com.pwroblew.photoed.lib.{EdImage, EdImageFiles, ImageStatus, PhotoEdAppState}

class LoadAction[F[_]: {MonadThrow, Console}](
    imageLoader: EdImageFiles[F]
) extends EditorActionBasic[F] {

  override def actB(
      state: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] = {

    if commandDetails.length != 3 then {
      val exception = new IllegalArgumentException(s"syntax: load <filename> <img-id>")
      exception.raiseError[F, AdditionalActions]
    } else {

      val cmd: String      = commandDetails.head
      val pathBase: String = commandDetails(1)
      val imageId: String  = commandDetails(2)

      val path: String = cmd match {
        case "load"     => pathBase
        case "load-res" => s"src/main/resources/$pathBase"
      }

      loadImage(imageLoader.load)(path, imageId)(state)
        >> AdditionalActions(List.empty[String], List("show")).pure[F]
    }
  }

  override def keywords: List[String] = List("load", "load-res")
}

object LoadAction {
  def apply[F[_]: {MonadThrow, Console}](using imageLoader: EdImageFiles[F]): LoadAction[F] =
    new LoadAction(imageLoader)

  def loadImage[F[_]: MonadThrow](edImageLoader: String => F[EdImage])(
      path: String,
      imageId: String
  )(
      appState: Ref[F, PhotoEdAppState[F]]
  )
      : F[Unit] = {
    for {
      imageLoaded <- edImageLoader(path)
      _           <- appState.update(state =>
                       state.copy(
                         history = state.history :+ s"[loaded: $path]",
                         imagesStatus = state.imagesStatus :+ ImageStatus(imageId, imageLoaded, false, false)
                       )
                     )
    } yield ()
  }
}
