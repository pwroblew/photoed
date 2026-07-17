package com.pwroblew.photoed.lib.actions.action_definitions

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState
import com.pwroblew.photoed.lib.actions.ActionKeyword.HIDE
import com.pwroblew.photoed.lib.actions.{ActionKeyword, AdditionalActions, EditorActionShowable}
import com.pwroblew.photoed.lib.impl_f.WindowsManager

class HideAction[F[_]: {MonadThrow, Console}] extends EditorActionShowable[F] {

  override def act(
      state: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String],
      windowsManager: WindowsManager[F]
  ): F[AdditionalActions] = {

    val maybeId: Option[String] = commandDetails.tail.headOption

    val value: OptionT[F, Unit] = for {
      viewerWindow <- OptionT(windowsManager.windowsRefs.get.map(windows =>
                        maybeId match {
                          case None     => windows.headOption.map(_._2)
                          case Some(id) => windows.get(id)
                        }
                      ))

      _ <- OptionT.liftF(viewerWindow.imageWindow.hide(state))
    } yield ()
    value.value >> AdditionalActions.empty.pure[F]
  }

  override def keywords: List[ActionKeyword] = List(HIDE)
}

object HideAction {
  def apply[F[_]: {MonadThrow, Console}]: HideAction[F] = new HideAction()
}
