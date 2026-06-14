package com.pwroblew.photoed.lib.actions

import cats.Applicative
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState

class ClearAction[F[_]: Applicative] extends EditorAction[F] {

  override def run(
      state: PhotoEdAppState,
      commandDetails: List[String]
  ): F[(Boolean, PhotoEdAppState)] = {

    (true, PhotoEdAppState.initialState).pure[F]
  }

}
