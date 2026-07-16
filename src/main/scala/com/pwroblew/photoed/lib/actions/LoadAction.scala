package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.{Ref, Resource}
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.actions.LoadAction.loadImage
import com.pwroblew.photoed.lib.{EdImage, EdImageFiles, EdImageViewer, PhotoEdAppState}

class LoadAction[F[_]: {MonadThrow, Console}](
    imageLoader: EdImageFiles[F]
) extends EditorActionBasic[F] {

  override def actB(
      state: Ref[F, PhotoEdAppState],
      commandDetails: List[String]
  ): F[AdditionalActions] = {
    val path: Option[String] = commandDetails.drop(1).headOption
    loadImage(imageLoader.load)(path)(state) >> AdditionalActions(
      List.empty[String],
      List("show")
    ).pure[F]
  }

  override def keywords: List[String] = List("load")
}

object LoadAction {
  def apply[F[_]: {MonadThrow, Console}](using imageLoader: EdImageFiles[F]): LoadAction[F] =
    new LoadAction(imageLoader)

  def loadImage[F[_]: MonadThrow](edImageLoader: String => F[EdImage])(path: Option[String])(
      appState: Ref[F, PhotoEdAppState]
  )
      : F[Unit] = {
    for {
      imageLoaded <- path.traverse(edImageLoader(_))
      _           <- appState.update(state =>
                       state.copy(
                         history = List(s"[loaded: $path]"),
                         edImage = imageLoaded,
                         toBeContinued = true
                       )
                     )
    } yield ()
  }
}
