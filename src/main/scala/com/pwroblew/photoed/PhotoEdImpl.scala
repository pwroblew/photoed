package com.pwroblew.photoed

import cats.Applicative.ops.toAllApplicativeOps
import cats.MonadThrow
import cats.effect.{IO, Ref}
import cats.implicits.{catsSyntaxOptionId, toFlatMapOps}

case class PhotoAppState(imageDesc: Option[String])
object PhotoAppState {
  val initState: PhotoAppState = PhotoAppState(Option.empty)
}

trait PhotoEd[F[_]] {
  def process(command: String, appState: Ref[F, PhotoAppState])(printString: String => F[Unit]): F[Unit]
}


class PhotoEdImpl[F[_] : MonadThrow] extends PhotoEd[F] {

  type StringPrint = String => F[Unit]

  override def process(
      command: String,
      appState: Ref[F, PhotoAppState]
  )(using printString: StringPrint): F[Unit] = command match {
    case "load" => for {
        _         <- appState.update(_ => PhotoAppState("[loaded]".some))
        imageDesc <- appState.get.map(_.imageDesc)
        _         <- printString(s"Image description: $imageDesc")
      } yield ()

    case "blur" => for {
        _         <- appState.update(state =>
                       state.copy(imageDesc = state.imageDesc.map(_ + "[blurred]"))
                     )
        imageDesc <- appState.get.map(_.imageDesc)
        _         <- printString(s"Image description: $imageDesc")
      } yield ()

    case cmd: String =>
      MonadThrow[F].raiseError(new Exception(s"Unsupported image processing command: \"$cmd\""))
  }

}

object PhotoEdImpl {
  def apply[F[_] : MonadThrow]: PhotoEdImpl[F] = new PhotoEdImpl[F]()
}
