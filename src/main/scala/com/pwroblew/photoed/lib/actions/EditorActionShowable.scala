package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.data.StateT
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState
import com.pwroblew.photoed.lib.impl_f.{WindowsManager, WindowsMap}

trait EditorActionShowable[F[_]: {MonadThrow, Console}] {

  def act(
      state: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String],
      windowsManager: WindowsManager[F]
  ): StateT[F, WindowsMap[F], AdditionalActions]

  def processCmd(
      state: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String],
      windowsManager: WindowsManager[F]
  ): StateT[F, WindowsMap[F], AdditionalActions] = {
    if commandDetails.tail.headOption.contains("help") then help
    else act(state, commandDetails, windowsManager)
  }

  def keywords: List[ActionKeyword]

  protected def maybeImageId(commandDetails: List[String]): Option[String] = commandDetails.tail.headOption

  protected def help: StateT[F, WindowsMap[F], AdditionalActions]

}

object EditorActionShowable {
  def emptyAction[F[_]: {MonadThrow, Console}]: EditorActionShowable[F] =
    new EditorActionShowable[F] {

      override def act(
          state: Ref[F, PhotoEdAppState[F]],
          commandDetails: List[String],
          windowsManager: WindowsManager[F]
      ): StateT[F, WindowsMap[F], AdditionalActions] =
        AdditionalActions(List.empty, List.empty).pure

      override def keywords: List[ActionKeyword] = List.empty[ActionKeyword]

      override protected def help: StateT[F, WindowsMap[F], AdditionalActions] =
        AdditionalActions(List.empty, List.empty).pure
    }
}
