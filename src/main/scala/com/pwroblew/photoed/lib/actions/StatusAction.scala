package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.data.{EitherT, OptionT}
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.{EdImage, EdImageFiles, PhotoEdAppState}

class StatusAction[F[_]: {Console, MonadThrow}] extends EditorAction[F] {

  private val indent: String = " " * 4

  override def run(
      state: PhotoEdAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoEdAppState)] = {

    val status: List[String]                                     = state.stateStatus
    val nelStatusOrError: Either[RuntimeException, List[String]] =
      Option.when(status.nonEmpty)(status).toRight(new RuntimeException("no status to be printed"))

    val res: EitherT[F, RuntimeException, Unit] = for {
      status <- EitherT.fromEither(nelStatusOrError)
      _      <- EitherT.liftF(Console[F].println(status.map(indent + _).mkString("\n")))
    } yield ()

    res.rethrowT *> (true, state).pure[F]
  }

}
