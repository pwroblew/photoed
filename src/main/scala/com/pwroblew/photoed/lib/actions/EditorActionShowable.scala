package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState
import com.pwroblew.photoed.lib.impl_f.WindowsManager

trait EditorActionShowable[F[_]: {MonadThrow, Console}] {

  def act(
      state: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String],
      windowsManager: WindowsManager[F]
  ): F[AdditionalActions]

  def keywords: List[ActionKeyword]

}

object EditorActionShowable {
  def emptyAction[F[_]: {MonadThrow, Console}]: EditorActionShowable[F] =
    new EditorActionShowable[F] {

      override def act(
          state: Ref[F, PhotoEdAppState[F]],
          commandDetails: List[String],
          windowsManager: WindowsManager[F]
      ): F[AdditionalActions] =
        AdditionalActions(List.empty, List.empty).pure[F]

      override def keywords: List[ActionKeyword] = List.empty[ActionKeyword]
    }
}
