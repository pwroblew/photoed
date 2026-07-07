package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.Ref
import cats.syntax.all.*
import cats.effect.std.Console
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}
import com.pwroblew.photoed.lib.actions.EditorActionBasic.emptyActionB

trait EditorActionBasic[F[_]: MonadThrow: Console] extends EditorActionShowable[F] {

  override def act(
      state: Ref[F, PhotoEdAppState],
      commandDetails: List[String],
      imageViewer: EdImageViewer[F]
  ): F[Unit] =
    actB(state, commandDetails)

  override def next: EditorActionShowable[F] = nextB
  override def prev: EditorActionShowable[F] = prevB

  def runB(
      state: Ref[F, PhotoEdAppState],
      commandDetails: List[String]
  ): F[Unit] =
    for {
      _ <- prevB.runB(state, commandDetails)
      _ <- actB(state, commandDetails)
      _ <- nextB.runB(state, commandDetails)
    } yield ()

  def actB(state: Ref[F, PhotoEdAppState], commandDetails: List[String]): F[Unit]

  def nextB: EditorActionBasic[F] = emptyActionB
  def prevB: EditorActionBasic[F] = emptyActionB
}

object EditorActionBasic {
  def emptyActionB[F[_]: MonadThrow: Console]: EditorActionBasic[F] = new EditorActionBasic[F] {

    override def actB(
        state: Ref[F, PhotoEdAppState],
        commandDetails: List[String]
    ): F[Unit] = ().pure[F]

    override def runB(
        state: Ref[F, PhotoEdAppState],
        commandDetails: List[String]
    ): F[Unit] = actB(state, commandDetails)

    override def run(
        state: Ref[F, PhotoEdAppState],
        commandDetails: List[String],
        imageViewer: EdImageViewer[F]
    ): F[Unit] =
      runB(state, commandDetails)

  }
}
