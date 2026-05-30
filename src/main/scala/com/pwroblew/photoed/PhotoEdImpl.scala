package com.pwroblew.photoed

import cats.MonadThrow
import cats.effect.Ref
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxOptionId, toFlatMapOps}
import cats.syntax.functor.*
import com.pwroblew.photoed
import com.pwroblew.photoed.lib.{Image, PhotoAppState}

trait PhotoEd[F[_]] {
  def process(command: String, appState: Ref[F, PhotoAppState]): F[Boolean]
}

class PhotoEdImpl[F[_]: MonadThrow](
    private val imageLoader: String => F[Image],
    private val printString: String => F[Unit]
) extends PhotoEd[F] {

  override def process(command: String, appState: Ref[F, PhotoAppState]): F[Boolean] = {
    val commandDetails: Array[String] = command.split(" ")
    val action                        = commandDetails(0)

    action match {
      case "load" =>
        val path = commandDetails(1)
        for {
          imageLoaded <- imageLoader(path)
          _           <- appState.update(_ =>
                           PhotoAppState(
                             imageDesc = "[loaded]".some,
                             image = Some(imageLoaded)
                           )
                         )
          imageDesc   <- appState.get.map(_.imageDesc)
          _           <- printString(s"Image description: $imageDesc")
        } yield true

      case "blur" => for {
          _         <- appState.update(state =>
                         state.copy(imageDesc = state.imageDesc.map(_ + "[blurred]"))
                       )
          imageDesc <- appState.get.map(_.imageDesc)
          _         <- printString(s"Image description: $imageDesc")
        } yield true

      case "exit" => false.pure[F]

      case cmd: String =>
        MonadThrow[F].raiseError(new Exception(
          s"Error: Unsupported image processing command provided: \"$cmd\". Please provide \"exit\" to exit the app."
        ))
    }
  }

}

object PhotoEdImpl {
  def apply[F[_]: MonadThrow](
      imageLoader: String => F[Image],
      printString: String => F[Unit]
  ): PhotoEdImpl[F] =
    new PhotoEdImpl[F](imageLoader, printString)
}
