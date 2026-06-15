package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import com.pwroblew.photoed.lib.actions.SaveAction.saveImage
import com.pwroblew.photoed.lib.{EdImage, EdImageFiles, PhotoEdAppState}

class SaveResAction[F[_]: MonadThrow](imageLoader: EdImageFiles[F]) extends EditorAction[F] {

  override def run(
      state: PhotoEdAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoEdAppState)] = {
    val maybePath: Option[String] = commandDetails.drop(1).headOption.map("src/main/resources/" + _)
    saveImage(imageLoader.save)(state, maybePath)
  }
}
