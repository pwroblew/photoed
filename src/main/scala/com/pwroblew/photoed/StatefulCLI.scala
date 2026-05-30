package com.pwroblew.photoed

import cats.effect.{IO, IOApp, Ref}

type AppState = PhotoAppState
val initialState: PhotoAppState = PhotoAppState.initState

object StatefulCLI extends IOApp.Simple {

  override def run: IO[Unit] = for {
    initState <- Ref.of[IO, AppState](initialState)
    _         <- commandLoop(initState)
  } yield ()

  private def commandLoop(appState: Ref[IO, AppState]): IO[Unit] = {
    given (String => IO[Unit]) = IO.println
    for {
      cmd <- IO.print("Please provide a command: ") *> IO.readLine
      _   <- cmd match
               case "exit" => IO.unit
               case _      => PhotoEdImpl[IO].process(cmd, appState)
                   .handleErrorWith { case e: Exception =>
                     IO.println(s"${e.getMessage}. To exit type 'exit'.")
                   }
                   *> commandLoop(appState)

    } yield ()
  }

  private def printImageDesc(imageDesc: Option[String]): IO[Unit] = {
    IO.println(s"Image description: $imageDesc")
  }
}
