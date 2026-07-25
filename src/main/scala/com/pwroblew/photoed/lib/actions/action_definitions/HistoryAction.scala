package com.pwroblew.photoed.lib.actions.action_definitions

import cats.MonadThrow
import cats.data.EitherT
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState
import com.pwroblew.photoed.lib.actions.ActionKeyword.HISTORY
import com.pwroblew.photoed.lib.actions.{ActionKeyword, AdditionalActions, EditorActionBasic}

class HistoryAction[F[_]: {Console, MonadThrow}] extends EditorActionBasic[F] {

  private val indent: String = " " * 4

  override def actB(
      state: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] = {

    val res: EitherT[F, RuntimeException, Unit] = for {
      history <- EitherT(state.get.map(_.history).map(history =>
                   Option.when(history.nonEmpty)(
                     history
                   ).toRight(new RuntimeException("no history to be printed"))
                 ))
      _       <- EitherT.liftF(Console[F].println(history.map(indent + _).mkString("\n")))
    } yield ()

    res.rethrowT >> AdditionalActions.empty.pure[F]
  }

  override def keywords: List[ActionKeyword] = List(HISTORY)

  override protected def helpB: F[AdditionalActions] =
    Console[F].println("history: prints the history of entered commands")
      >> Console[F].println("syntax: history")
      >> AdditionalActions.empty.pure
}
