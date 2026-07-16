package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.{Ref, Resource}
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.impl_f.WindowHandle
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

class HideAction[F[_]: {MonadThrow, Console}] extends EditorActionShowable[F] {

  override def act(
      state: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String],
      windowHandle: WindowHandle[F]
  ): F[AdditionalActions] = { // imageViewer.hide(state) >> AdditionalActions.empty.pure[F]
    val value: OptionT[F, Unit] = for {
      viewerWindow <- OptionT(windowHandle.windowRef.get)
      _            <- OptionT.liftF(viewerWindow.viewer.hide(state))
    } yield ()
    value.value >> AdditionalActions.empty.pure[F]
  }

  override def keywords: List[String] = List("hide")
}

object HideAction {
  def apply[F[_]: {MonadThrow, Console}]: HideAction[F] = new HideAction()
}
