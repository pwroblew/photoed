package com.pwroblew.photoed.lib

trait PhotoEdApp[F[_]] {
  def process(command: String, appState: PhotoEdAppState): F[(Boolean, PhotoEdAppState)]
}
