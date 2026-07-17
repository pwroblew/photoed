package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState
import com.pwroblew.photoed.lib.impl_f.WindowsManager

trait EditorActionBasic[F[_]: {MonadThrow, Console}] extends EditorActionShowable[F] {

  override def act(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String],
      windowsManager: WindowsManager[F]
  ): F[AdditionalActions] =
    actB(stateRef, commandDetails)

  def actB(state: Ref[F, PhotoEdAppState[F]], commandDetails: List[String]): F[AdditionalActions]

}

object EditorActionBasic {
  def emptyActionB[F[_]: {MonadThrow, Console}]: EditorActionBasic[F] = new EditorActionBasic[F] {

    override def actB(
        stateRef: Ref[F, PhotoEdAppState[F]],
        commandDetails: List[String]
    ): F[AdditionalActions] =
      AdditionalActions(List.empty, List.empty).pure[F]

    override def keywords: List[ActionKeyword] = List.empty[ActionKeyword]
  }
}
