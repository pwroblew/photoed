package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.impl_f.WindowsManager
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

class DisplayAction[F[_]: {MonadThrow, Console}] extends EditorActionShowable[F] {

  override def act(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String],
      windowsManager: WindowsManager[F]
  ): F[AdditionalActions] = {

    val maybeId: Option[String] = commandDetails.tail.headOption

    val res: OptionT[F, Unit] = for {
      image <- OptionT(stateRef.get
                 .map(state => state.imagesStatus)
                 .map(list =>
                   maybeId match {
                     case None     => list.headOption
                     case Some(id) => list.find(_.id == id)
                   }
                 )
                 .map(_.map(_.image)))

      viewerWindow <- OptionT(windowsManager.windowsRefs.get.map(windows =>
                        maybeId match {
                          case None     => windows.headOption.map(_._2)
                          case Some(id) => windows.get(id)
                        }
                      ))
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
