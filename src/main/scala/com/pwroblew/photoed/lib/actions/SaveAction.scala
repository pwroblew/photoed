package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.data.OptionT
import com.pwroblew.photoed.lib.actions.SaveAction.saveImage
import com.pwroblew.photoed.lib.{EdImage, EdImageFiles, PhotoEdAppState}

class SaveAction[F[_]: MonadThrow](imageLoader: EdImageFiles[F]) extends EditorAction[F] {

  override def act(
      state: PhotoEdAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoEdAppState)] = {
    val maybePath: Option[String] = commandDetails.drop(1).headOption
    saveImage(imageLoader.save)(state, maybePath)
  }
}

object SaveAction {
  def saveImage[F[_]: MonadThrow](imageSaver: (EdImage, String) => F[Unit])(
      state: PhotoEdAppState,
      maybePath: Option[String]
  ): F[(Boolean, PhotoEdAppState)] = {

    val maybeImage: Option[EdImage] = state.edImage

    val res: OptionT[F, (Boolean, PhotoEdAppState)] = for {
      path  <- OptionT.fromOption[F](maybePath)
      image <- OptionT.fromOption[F](maybeImage)
      _     <- OptionT.liftF(imageSaver(image, path))
    } yield {
      val newState: PhotoEdAppState = state.copy(
        stateStatus = state.stateStatus :+ s"[saved to: $path]"
      )
      (true, newState)
    }

    res.getOrRaise(
      new IllegalArgumentException(s"Invalid arguments for saving the image. Path: [$maybePath]")
    )
  }

}
