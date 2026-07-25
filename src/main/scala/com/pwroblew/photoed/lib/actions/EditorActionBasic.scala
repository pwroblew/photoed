package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.data.StateT
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState
import com.pwroblew.photoed.lib.impl_f.{WindowsManager, WindowsMap}

trait EditorActionBasic[F[_]: {MonadThrow, Console}] extends EditorActionShowable[F] {

  override def act(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String],
      windowsManager: WindowsManager[F]
  ): StateT[F, WindowsMap[F], AdditionalActions] =
    StateT.liftF(actB(stateRef, commandDetails))

  def actB(state: Ref[F, PhotoEdAppState[F]], commandDetails: List[String]): F[AdditionalActions]

  override def help: StateT[F, WindowsMap[F], AdditionalActions] = StateT.liftF(helpB)

  protected def helpB: F[AdditionalActions]

}

object EditorActionBasic {
  def emptyActionB[F[_]: {MonadThrow, Console}]: EditorActionBasic[F] = new EditorActionBasic[F] {

    override def actB(
        stateRef: Ref[F, PhotoEdAppState[F]],
        commandDetails: List[String]
    ): F[AdditionalActions] =
      AdditionalActions(List.empty, List.empty).pure[F]

    override def keywords: List[ActionKeyword] = List.empty[ActionKeyword]

    override protected def helpB: F[AdditionalActions] = AdditionalActions(List.empty, List.empty).pure[F]
  }
}
