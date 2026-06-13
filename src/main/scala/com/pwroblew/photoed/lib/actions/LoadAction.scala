package com.pwroblew.photoed.lib.actions

import cats.Monad
import cats.syntax.all.*
import com.pwroblew.photoed.lib.actions.LoadAction.loadImage
import com.pwroblew.photoed.lib.{Action, Image, PhotoAppState}

class LoadAction[F[_]: Monad](imageLoader: String => F[Image]) extends Action[F] {

  override def run(
      state: PhotoAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoAppState)] = {
    val path: Option[String] = commandDetails.drop(1).headOption
    loadImage(imageLoader)(path)
  }

}

object LoadAction {
  def loadImage[F[_]: Monad](imageLoader: String => F[Image])(path: Option[String])
      : F[(Boolean, PhotoAppState)] = {
    for {
      imageLoaded <- path.traverse(imageLoader(_))
      newState    <- PhotoAppState(
                       imageDesc = s"[loaded: $path]".some,
                       image = imageLoaded
                     ).pure[F]
    } yield (true, newState)
  }
}
