package com.pwroblew.photoed.lib.actions

import com.pwroblew.photoed.lib.PhotoEdAppState

trait EditorAction[F[_]] {
  def run(state: PhotoEdAppState, commandDetails: List[String]): F[(Boolean, PhotoEdAppState)]
}
