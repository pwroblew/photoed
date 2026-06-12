package com.pwroblew.photoed.lib

trait PhotoEd[F[_]] {
  def process(command: String, appState: PhotoAppState): F[(Boolean, PhotoAppState)]
}
