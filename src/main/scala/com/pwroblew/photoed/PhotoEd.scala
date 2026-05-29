package com.pwroblew.photoed

import cats.effect.{IO, IOApp, Ref}
import cats.syntax.option.catsSyntaxOptionId

case class AppState(imageDesc: Option[String])

object PhotoEd extends IOApp.Simple {

  override def run: IO[Unit] = for {
    initState <- Ref.of[IO, AppState](AppState(Option.empty))
    _         <- commandLoop(initState)
  } yield ()

  private def commandLoop(appState: Ref[IO, AppState]): IO[Unit] = {
    for {
      cmd <- IO.print("Please provide a command: ") *> IO.readLine
      _   <- cmd match
               case "exit" => IO.unit
               case _      => process(cmd, appState).handleErrorWith { case e: Exception =>
                   IO.println(s"${e.getMessage}. To exit type 'exit'.")
                 }
                   *> commandLoop(appState)

    } yield ()
  }

  private def process(command: String, appState: Ref[IO, AppState]): IO[Unit] = command match {
    case "load" => for {
        _         <- appState.update(_ => AppState("[loaded]".some))
        imageDesc <- appState.get.map(_.imageDesc)
        _         <- printImageDesc(imageDesc)
      } yield ()

    case "blur" => for {
        _         <- appState.update(state =>
                       state.copy(imageDesc = state.imageDesc.map(_ + "[blurred]"))
                     )
        imageDesc <- appState.get.map(_.imageDesc)
        _         <- printImageDesc(imageDesc)
      } yield ()

    case cmd: String => IO.raiseError(new Exception(s"Unsupported image processing command: \"$cmd\""))
  }

  private def printImageDesc(imageDesc: Option[String]): IO[Unit] = {
    IO.println(s"Image description: $imageDesc")
  }
}
