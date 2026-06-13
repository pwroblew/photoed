package com.pwroblew.photoed.lib.actions

import cats.Applicative
import cats.implicits.catsSyntaxApplicativeId
import com.pwroblew.photoed.lib.{Action, PhotoAppState}

final class ExitAction[F[_]: Applicative] extends Action[F] {
  def run(state: PhotoAppState, commandDetails: List[String]): F[(Boolean, PhotoAppState)] = {

    val newState = PhotoAppState(
      imageDesc = state.imageDesc.map(_ + "[exiting]"),
      image = state.image
    )
    (false, newState).pure[F]

  }
}
