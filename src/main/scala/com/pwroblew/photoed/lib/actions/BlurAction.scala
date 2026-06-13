package com.pwroblew.photoed.lib.actions

import cats.Applicative
import cats.implicits.catsSyntaxApplicativeId
import com.pwroblew.photoed.lib.{Action, PhotoAppState}

class BlurAction[F[_] : Applicative] extends Action[F] {
  override def run(
      state: PhotoAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoAppState)] = {
    
    val newState = PhotoAppState(
      imageDesc = state.imageDesc.map(_ + "[blurred]"),
      image = state.image
    )
    (true, newState).pure[F]
  }
}
