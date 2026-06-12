package com.pwroblew.photoed.lib.impl

import cats.MonadThrow
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxOptionId, toFlatMapOps}
import cats.syntax.all.*
import com.pwroblew.photoed
import com.pwroblew.photoed.lib.{Image, ImageViewer, PhotoAppState, PhotoEd}

class PhotoEdImpl[F[_]: MonadThrow](
    private val imageLoader: String => F[Image],
    private val printString: String => F[Unit],
    private val imageViewer: ImageViewer[F]
) extends PhotoEd[F] {

  override def process(
      command: String,
      appState: PhotoAppState
  ): F[(Boolean, PhotoAppState)] = {
    val commandDetails: Array[String] = command.split(" ")
    val action: Option[String]        = commandDetails.headOption

    action match {
      case Some("load") =>
        val path: Option[String] = commandDetails.drop(1).headOption
        for {
          imageLoaded <- path.traverse(imageLoader(_))
          _           <- imageLoaded.traverse(imageViewer.show)
          newState    <- PhotoAppState(
                           imageDesc = "[loaded]".some,
                           image = imageLoaded
                         ).pure[F]
          _           <- printString(s"Image description: ${newState.imageDesc.orEmpty}")
        } yield (true, newState)

      case Some("load-res") =>
        val path: Option[String] = commandDetails.drop(1).headOption.map("src/main/resources/" + _)
        for {
          imageLoaded <- path.traverse(imageLoader(_))
          _           <- imageLoaded.traverse(imageViewer.show)
          newState    <- PhotoAppState(
                           imageDesc = s"[loaded (${commandDetails(1)})]".some,
                           image = imageLoaded
                         ).pure[F]
          _           <- printString(s"Image description: ${newState.imageDesc.orEmpty}")
        } yield (true, newState)

      case Some("blur") => for {
          newState <- PhotoAppState(
                        imageDesc = appState.imageDesc.map(_ + "[blurred]"),
                        image = appState.image
                      ).pure[F]
          _        <- printString(s"Image description: ${newState.imageDesc.orEmpty}")
        } yield (true, newState)

      case Some("exit") => (false, appState).pure[F]

      case _ =>
        MonadThrow[F].raiseError(new IllegalArgumentException(
          s"Error: Unsupported image processing command provided: \"$command\". Please provide \"exit\" to exit the app."
        ))
    }
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
