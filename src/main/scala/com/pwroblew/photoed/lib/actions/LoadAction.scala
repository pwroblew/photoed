package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.{Ref, Resource}
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.actions.LoadAction.loadImage
import com.pwroblew.photoed.lib.{EdImage, EdImageFiles, EdImageViewer, PhotoEdAppState}

class LoadAction[F[_]: MonadThrow: Console](
    imageLoader: EdImageFiles[F]
) extends EditorActionBasic[F] {

  override def actB(
      state: Ref[F, PhotoEdAppState],
      commandDetails: List[String]
  ): F[Unit] = {
    val path: Option[String] = commandDetails.drop(1).headOption
    loadImage(imageLoader.load)(path)(state)
  }

  override def next: EditorActionShowable[F] = new ShowAction[F]()

}

object LoadAction {
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
