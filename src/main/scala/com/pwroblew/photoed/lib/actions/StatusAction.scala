package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.data.EitherT
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState

class StatusAction[F[_]: {Console, MonadThrow}] extends EditorActionBasic[F] {

  private val indent: String = " " * 4

  override def actB(
      state: Ref[F, PhotoEdAppState],
      commandDetails: List[String]
  ): F[AdditionalActions] = {

    val res: EitherT[F, RuntimeException, Unit] = for {
      status <- EitherT(state.get.map(_.history).map(status =>
                  Option.when(status.nonEmpty)(
                    status
                  ).toRight(new RuntimeException("no status to be printed"))
                ))
      _      <- EitherT.liftF(Console[F].println(status.map(indent + _).mkString("\n")))
      _      <- EitherT.liftF(state.get.flatMap(st => Console[F].println(st)))
      _      <- EitherT.liftF(state.update(_.copy(toBeContinued = true)))
    } yield ()

    res.rethrowT >> AdditionalActions.empty.pure[F]
  }

  override def keywords: List[String] = List("status")
}
