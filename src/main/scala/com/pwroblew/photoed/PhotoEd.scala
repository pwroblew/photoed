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
      command <- IO.print("Please provide a command: ") *> IO.readLine
      _       <- process(command, appState)
    } yield ()
  }

  private def process(command: String, appState: Ref[IO, AppState]): IO[Unit] = command match {
    case "load" => for {
        loadedNew <- Ref.of[IO, AppState](AppState("[loaded]".some))
        imageDesc <- loadedNew.get.map(_.imageDesc)
        _         <- printImageDesc(imageDesc)
        _         <- commandLoop(loadedNew)
      } yield ()

    case "blur" => for {
        _         <- appState.update(state =>
                       state.copy(imageDesc = state.imageDesc.map(str => str + "[blurred]"))
                     )
        imageDesc <- appState.get.map(_.imageDesc)
        _         <- printImageDesc(imageDesc)
        _         <- commandLoop(appState)
      } yield ()

    case "exit" => IO.unit

    case _ => IO.println("Unsupported command. To exit type 'exit'.") *> commandLoop(appState)
  }

  private def printImageDesc(imageDesc: Option[String]): IO[Unit] = {
    IO.println(s"Image description: $imageDesc")
  }
}
