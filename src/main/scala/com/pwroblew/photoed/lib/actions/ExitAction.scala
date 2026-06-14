package com.pwroblew.photoed.lib.actions

import cats.Applicative
import cats.implicits.catsSyntaxApplicativeId
import com.pwroblew.photoed.lib.PhotoEdAppState

final class ExitAction[F[_]: Applicative] extends EditorAction[F] {
  def run(state: PhotoEdAppState, commandDetails: List[String]): F[(Boolean, PhotoEdAppState)] = {

    val newState: PhotoEdAppState = state.copy(
      stateStatus = state.stateStatus :+ "[exiting]"
    )
    (false, newState).pure[F]

  }
}
