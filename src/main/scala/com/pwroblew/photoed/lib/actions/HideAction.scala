package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.{Ref, Resource}
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.impl_f.WindowsManager
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

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

      _ <- OptionT.liftF(viewerWindow.viewer.hide(state))
    } yield ()
    value.value >> AdditionalActions.empty.pure[F]
  }

  override def keywords: List[String] = List("hide")
}

object HideAction {
  def apply[F[_]: {MonadThrow, Console}]: HideAction[F] = new HideAction()
}
