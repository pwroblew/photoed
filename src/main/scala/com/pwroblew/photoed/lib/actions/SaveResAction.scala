package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.Ref
import cats.effect.std.Console
import com.pwroblew.photoed.lib.actions.SaveAction.saveImage
import com.pwroblew.photoed.lib.{EdImage, EdImageFiles, PhotoEdAppState}

class SaveResAction[F[_]: MonadThrow: Console](imageLoader: EdImageFiles[F])
    extends EditorActionBasic[F] {

  override def actB(
      state: Ref[F, PhotoEdAppState],
      commandDetails: List[String]
  ): F[Unit] = {
    val maybePath: Option[String] = commandDetails.drop(1).headOption.map("src/main/resources/" + _)
    saveImage(imageLoader.save)(state, maybePath)
  }
}
