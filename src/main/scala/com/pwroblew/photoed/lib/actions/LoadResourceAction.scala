package com.pwroblew.photoed.lib.actions

import cats.Monad
import cats.syntax.all.*
import com.pwroblew.photoed.lib.actions.LoadAction.loadImage
import com.pwroblew.photoed.lib.{Action, Image, PhotoAppState}

class LoadResourceAction[F[_]: Monad](imageLoader: String => F[Image]) extends Action[F] {

  override def run(
      state: PhotoAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoAppState)] = {
    val path: Option[String] = commandDetails.drop(1).headOption.map("src/main/resources/" + _)
    loadImage(imageLoader)(path)
  }

}
