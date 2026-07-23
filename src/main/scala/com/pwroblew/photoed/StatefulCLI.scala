package com.pwroblew.photoed

import cats.data.{IndexedStateT, StateT}
import cats.effect.std.Dispatcher
import cats.effect.{IO, IOApp, Ref, Resource}
import cats.implicits.{catsSyntaxApplicativeError, catsSyntaxMonad}
import com.pwroblew.photoed.lib.*
import com.pwroblew.photoed.lib.impl_f.{PhotoEdAppImpl, WindowsManager, WindowsMap}
import com.pwroblew.photoed.lib.impl_io.{ImageFileMgmntImpl, ImageWindowImpl}

object StatefulCLI extends IOApp.Simple {

  type MakeImageWindowResource[F[_]] =
    (String, Dispatcher[F]) => Resource[F, ImageWindow[F]]

  given ImageFileMgmnt[IO]          = ImageFileMgmntImpl
  given MakeImageWindowResource[IO] = ImageWindowImpl.makeResource

  val app = PhotoEdAppImpl[IO]

  override def run: IO[Unit] = {

    val dispatcherRes: Resource[IO, Dispatcher[IO]] = Dispatcher.parallel[IO]

    val program: StateT[IO, WindowsMap[IO], Unit] = for {
      stateRef                          <- StateT.liftF(IO.ref(PhotoEdAppState.initialState[IO]))
      (winManager, winManagerFinalizer) <- StateT.liftF(WindowsManager.makeResource(dispatcherRes).allocated)
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
      _ <- StateT.liftF(app.readCommand(stateRef))
      _ <- app.nextStep(stateRef, winManager)
             .whileM_(StateT.liftF(stateRef.get.map(_.commands.nonEmpty)))
             .handleErrorWith(e => StateT.liftF(IO.println(e.getMessage)))
    } yield ()

}
