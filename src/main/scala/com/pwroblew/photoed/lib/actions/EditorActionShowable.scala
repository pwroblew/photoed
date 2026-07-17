package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.Ref
import cats.syntax.all.*
import cats.effect.std.Console
import com.pwroblew.photoed.lib.actions.EditorActionShowable.emptyAction
import com.pwroblew.photoed.lib.impl_f.WindowsManager
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

case class AdditionalActions(preActions: List[String], postActions: List[String])
object AdditionalActions {
  def empty: AdditionalActions = AdditionalActions(List.empty[String], List.empty[String])
}

trait EditorActionShowable[F[_]: {MonadThrow, Console}] {

  def act(
           state: Ref[F, PhotoEdAppState[F]],
           commandDetails: List[String],
           windowsManager: WindowsManager[F]
  ): F[AdditionalActions]

  def keywords: List[String]

}

object EditorActionShowable {
  def emptyAction[F[_]: {MonadThrow, Console}]: EditorActionShowable[F] =
    new EditorActionShowable[F] {

      override def act(
                        state: Ref[F, PhotoEdAppState[F]],
                        commandDetails: List[String],
                        windowsManager: WindowsManager[F]
      ): F[AdditionalActions] = AdditionalActions(List.empty[String], List.empty[String]).pure[F]

      override def keywords: List[String] = List.empty[String]
    }
}
