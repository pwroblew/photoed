package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.syntax.all.*
import com.pwroblew.photoed.lib.actions.LoadAction.loadImage
import com.pwroblew.photoed.lib.{EdImage, EdImageFiles, EdImageViewer, PhotoEdAppState}

class LoadAction[F[_]: MonadThrow](imageLoader: EdImageFiles[F], imageViewer: EdImageViewer[F])
    extends EditorAction[F] {

  override def act(
      state: PhotoEdAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoEdAppState)] = {
    val path: Option[String] = commandDetails.drop(1).headOption
    loadImage(imageLoader.load)(path)(state)
  }

  override def next: EditorAction[F] = new ShowAction[F](imageViewer)

}

object LoadAction {
  def loadImage[F[_]: MonadThrow](edImageLoader: String => F[EdImage])(path: Option[String])(
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
