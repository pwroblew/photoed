package com.pwroblew.photoed.lib.impl

import cats.MonadThrow
import cats.data.OptionT
import cats.implicits.*
import cats.syntax.all.*
import com.pwroblew.photoed
import com.pwroblew.photoed.lib.*
import com.pwroblew.photoed.lib.actions.{BlurAction, ExitAction, LoadAction, LoadResourceAction}

final class PhotoEdImpl[F[_]: MonadThrow](
    private val imageLoader: String => F[Image],
    private val printString: String => F[Unit],
    private val imageViewer: ImageViewer[F]
) extends PhotoEd[F] {

  override def process(
      command: String,
      appState: PhotoAppState
  ): F[(Boolean, PhotoAppState)] = {

    val actions: Map[String, Action[F]] = Map(
      "load"     -> new LoadAction[F](imageLoader),
      "load-res" -> new LoadResourceAction[F](imageLoader),
      "blur"     -> new BlurAction[F],
      "exit"     -> new ExitAction[F]
    )

    val commandDetails: List[String] = command.split(" ").toList

    for {
      action           <-
        OptionT.fromOption[F](commandDetails.headOption).subflatMap(actions.get)
          .getOrRaise(new IllegalArgumentException(
            s"Error: Unsupported image processing command provided: \"$command\". Please provide \"exit\" to exit the app."
          ))
      (cont, newState) <- action.run(appState, commandDetails)
      _                <- newState.image.traverse(imageViewer.show)
      _                <- printString(s"Image description: ${newState.imageDesc.orEmpty}")
    } yield (cont, newState)

  }

}

object PhotoEdImpl {
  def apply[F[_]: MonadThrow](
      imageLoader: String => F[Image],
      printString: String => F[Unit],
      imageViewer: ImageViewer[F]
  ): PhotoEdImpl[F] =
    new PhotoEdImpl[F](imageLoader, printString, imageViewer)
}
