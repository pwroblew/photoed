package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.Ref
import cats.syntax.all.*
import cats.effect.std.Console
import com.pwroblew.photoed.lib.actions.EditorActionShowable.emptyAction
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

trait EditorActionShowable[F[_]: {MonadThrow, Console}] {

  def act(
      state: Ref[F, PhotoEdAppState],
      commandDetails: List[String],
      imageViewer: EdImageViewer[F]
  ): F[Unit]

  def run(
      state: Ref[F, PhotoEdAppState],
      commandDetails: List[String],
      imageViewer: EdImageViewer[F]
  ): F[Unit] =
    for {
      _ <- prev.run(state, commandDetails, imageViewer)
      _ <- act(state, commandDetails, imageViewer)
      _ <- next.run(state, commandDetails, imageViewer)
    } yield ()

  def next: EditorActionShowable[F] = emptyAction
  def prev: EditorActionShowable[F] = emptyAction

}

object EditorActionShowable {
  def emptyAction[F[_]: MonadThrow: Console]: EditorActionShowable[F] =
    new EditorActionShowable[F] {

      override def act(
          state: Ref[F, PhotoEdAppState],
          commandDetails: List[String],
          imageViewer: EdImageViewer[F]
      ): F[Unit] = ().pure[F]

      override def run(
          state: Ref[F, PhotoEdAppState],
          commandDetails: List[String],
          imageViewer: EdImageViewer[F]
      ): F[Unit] = act(state, commandDetails, imageViewer)

    }
}
