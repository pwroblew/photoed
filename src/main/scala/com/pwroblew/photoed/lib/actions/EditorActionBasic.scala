package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.Ref
import cats.syntax.all.*
import cats.effect.std.Console
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}
import com.pwroblew.photoed.lib.actions.EditorActionBasic.emptyActionB
import com.pwroblew.photoed.lib.impl_f.WindowsManager

trait EditorActionBasic[F[_]: {MonadThrow, Console}] extends EditorActionShowable[F] {

  override def act(
                    state: Ref[F, PhotoEdAppState[F]],
                    commandDetails: List[String],
                    windowsManager: WindowsManager[F]
  ): F[AdditionalActions] =
    actB(state, commandDetails)

  def actB(state: Ref[F, PhotoEdAppState[F]], commandDetails: List[String]): F[AdditionalActions]

}

object EditorActionBasic {
  def emptyActionB[F[_]: {MonadThrow, Console}]: EditorActionBasic[F] = new EditorActionBasic[F] {

    override def actB(
        state: Ref[F, PhotoEdAppState[F]],
        commandDetails: List[String]
    ): F[AdditionalActions] = AdditionalActions(List.empty[String], List.empty[String]).pure[F]

    override def keywords: List[String] = List.empty[String]
  }
}
