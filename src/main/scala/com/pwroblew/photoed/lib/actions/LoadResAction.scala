package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import com.pwroblew.photoed.lib.actions.LoadAction.loadImage
import com.pwroblew.photoed.lib.{EdImageFiles, EdImageViewer, PhotoEdAppState}

class LoadResAction[F[_]: MonadThrow](imageLoader: EdImageFiles[F], imageViewer: EdImageViewer[F])
    extends EditorAction[F] {

  override def act(
      state: PhotoEdAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoEdAppState)] = {
    val path: Option[String] = commandDetails.drop(1).headOption.map("src/main/resources/" + _)
    loadImage(imageLoader.load)(path)(state)
  }

  override def next: EditorAction[F] = new ShowAction[F](imageViewer)

}
