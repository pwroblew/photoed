package com.pwroblew.photoed.lib.actions

import cats.Monad
import cats.syntax.all.*
import com.pwroblew.photoed.lib.actions.LoadAction.loadImage
import com.pwroblew.photoed.lib.{EdImage, EdImageFiles, PhotoEdAppState}

class LoadAction[F[_]: Monad](imageLoader: EdImageFiles[F]) extends EditorAction[F] {

  override def run(
      state: PhotoEdAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoEdAppState)] = {
    val path: Option[String] = commandDetails.drop(1).headOption
    loadImage(imageLoader.load)(path)(state)
  }

}

object LoadAction {
  def loadImage[F[_]: Monad](edImageLoader: String => F[EdImage])(path: Option[String])(
      appState: PhotoEdAppState
  )
      : F[(Boolean, PhotoEdAppState)] = {
    for {
      imageLoaded <- path.traverse(edImageLoader(_))
      newState    <- appState.copy(
                       stateStatus = List(s"[loaded: $path]"),
                       edImage = imageLoaded
                     ).pure[F]
    } yield (true, newState)
  }
}
