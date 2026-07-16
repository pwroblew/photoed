package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.{IO, Ref, Resource}
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.impl_f.{ViewerWindow, WindowHandle}
import com.pwroblew.photoed.lib.impl_io.EdImageViewerImpl
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

class ShowAction[F[_]: {MonadThrow, Console}](using
    makeImageWindowResource: String => Resource[F, EdImageViewer[F]]
) extends EditorActionShowable[F] {

  override def act(
      appState: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String],
      windowHandle: WindowHandle[F]
  ): F[AdditionalActions] = for {
    _ <- appState.update(state =>
           state.copy(
             isShowing = true,
             toBeShown = true
           )
         )
    _ <- windowHandle.open(makeImageWindowResource("blabla"))
  } yield AdditionalActions(List.empty[String], List("display"))

  override def keywords: List[String] = List("show")
}

object ShowAction {
  def apply[F[_]: {MonadThrow, Console}](using
      makeImageWindowResource: String => Resource[F, EdImageViewer[F]]
  ): ShowAction[F] = new ShowAction()
}
