package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.impl_f.WindowHandle
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

class DisplayAction[F[_]: {MonadThrow, Console}] extends EditorActionShowable[F] {

  override def act(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String],
      windowHandle: WindowHandle[F]
  ): F[AdditionalActions] = {

    val res: OptionT[F, Unit] = for {
      image        <- OptionT(stateRef.get.map(state => state.imagesStatus.headOption.map(_.image)))
      viewerWindow <- OptionT(windowHandle.windowRef.get)
      _            <- OptionT.liftF(viewerWindow.viewer.show(stateRef)(image))
    } yield ()

    res.getOrRaise(new RuntimeException("Can't show the image. The image hasn't been loaded"))
      >> AdditionalActions.empty.pure[F]
  }

  override def keywords: List[String] = List("display")
}

object DisplayAction {
  def apply[F[_]: {MonadThrow, Console}]: DisplayAction[F] = new DisplayAction[F]()
}
