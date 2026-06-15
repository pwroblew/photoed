package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.data.OptionT
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

class DisplayAction[F[_]: MonadThrow](imageViewer: EdImageViewer[F]) extends EditorAction[F] {

  override def act(
      state: PhotoEdAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoEdAppState)] = {

    val res: OptionT[F, (Boolean, PhotoEdAppState)] = for {
      image <- OptionT.fromOption[F](state.edImage)
      state <- OptionT.liftF(imageViewer.show(state)(image))
    } yield (true, state)

    res.getOrRaise(new RuntimeException("Can't show the image."))
  }
}
