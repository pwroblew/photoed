package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

class DisplayAction[F[_]: {MonadThrow, Console}] extends EditorActionShowable[F] {

  override def act(
      state: Ref[F, PhotoEdAppState],
      commandDetails: List[String],
      imageViewer: EdImageViewer[F]
  ): F[Unit] = {

    val res: OptionT[F, Unit] = for {
      image <- OptionT(state.get.map(_.edImage))
      state <- OptionT.liftF(imageViewer.show(state)(image))
    } yield ()

    res.getOrRaise(new RuntimeException("Can't show the image. The image hasn't been loaded")).void
  }
}
