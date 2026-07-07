package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.Ref
import cats.syntax.all.*
import cats.effect.std.Console
import com.pwroblew.photoed.lib.actions.SaveAction.saveImage
import com.pwroblew.photoed.lib.{EdImage, EdImageFiles, PhotoEdAppState}

class SaveAction[F[_]: MonadThrow: Console](imageLoader: EdImageFiles[F])
    extends EditorActionBasic[F] {

  override def actB(
      state: Ref[F, PhotoEdAppState],
      commandDetails: List[String]
  ): F[Unit] = {
    val maybePath: Option[String] = commandDetails.drop(1).headOption
    saveImage(imageLoader.save)(state, maybePath)
  }
}

object SaveAction {
  def saveImage[F[_]: MonadThrow](imageSaver: (EdImage, String) => F[Unit])(
      appState: Ref[F, PhotoEdAppState],
      maybePath: Option[String]
  ): F[Unit] = {

    val res: OptionT[F, Unit] = for {
      image <- OptionT(appState.get.map(_.edImage))
      path  <- OptionT.fromOption[F](maybePath)
      _     <- OptionT.liftF(imageSaver(image, path))
      _     <- OptionT.liftF(appState.update(state =>
                 state.copy(
                   history = state.history :+ s"[saved to: $path]",
                   toBeContinued = true
                 )
               ))
    } yield ()

    res.getOrRaise(
      new IllegalArgumentException(
        s"Invalid arguments for saving the image. Path: [$maybePath] or image not loaded."
      )
    )
  }

}
