package com.pwroblew.photoed

import cats.data.{IndexedStateT, StateT}
import cats.effect.{IO, IOApp, Ref, Resource}
import cats.implicits.{catsSyntaxApplicativeError, catsSyntaxMonad}
import com.pwroblew.photoed.lib.*
import com.pwroblew.photoed.lib.impl_f.{PhotoEdAppImpl, WindowsManager, WindowsMap}
import com.pwroblew.photoed.lib.impl_io.{ImageFileMgmntImpl, ImageWindowImpl}

object StatefulCLI extends IOApp.Simple {

  given ImageFileMgmnt[IO]                        = ImageFileMgmntImpl
  given (String => Resource[IO, ImageWindow[IO]]) = ImageWindowImpl.makeResource

  val app = PhotoEdAppImpl[IO]

  override def run: IO[Unit] = {

    val program: StateT[IO, WindowsMap[IO], Unit] = for {
      stateRef                          <- StateT.liftF(IO.ref(PhotoEdAppState.initialState[IO]))
      (winManager, winManagerFinalizer) <- StateT.liftF(WindowsManager.makeResource[IO].allocated)
      _                                 <- basicAppLoop(stateRef, winManager).whileM_(TO_BE_CONTINUED(stateRef))
      _                                 <- StateT.liftF(winManagerFinalizer)
    } yield ()

    program.runA(Map.empty)
  }

  private def basicAppLoop(
      stateRef: Ref[IO, PhotoEdAppState[IO]],
      winManager: WindowsManager[IO]
  ): StateT[IO, WindowsMap[IO], Unit] =
    for {
      _ <- StateT.liftF[IO, WindowsMap[IO], Unit](app.readCommand(stateRef))
      _ <- app.nextStep(stateRef, winManager)
             .whileM_(StateT.liftF(stateRef.get.map(_.commands.nonEmpty)))
             .handleErrorWith(e => StateT.liftF(IO.println(e.getMessage)))
    } yield ()

}
