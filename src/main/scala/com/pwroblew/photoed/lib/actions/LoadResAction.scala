package com.pwroblew.photoed.lib.actions

import cats.Monad
import com.pwroblew.photoed.lib.actions.LoadAction.loadImage
import com.pwroblew.photoed.lib.{EdImageFiles, PhotoEdAppState}

class LoadResAction[F[_]: Monad](imageLoader: EdImageFiles[F]) extends EditorAction[F] {

  override def run(
      state: PhotoEdAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoEdAppState)] = {
    val path: Option[String] = commandDetails.drop(1).headOption.map("src/main/resources/" + _)
    loadImage(imageLoader.load)(path)(state)
  }

}
