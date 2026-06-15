package com.pwroblew.photoed.lib

trait EdImageViewer[F[_]] {
  def show(appState: PhotoEdAppState)(edImage: EdImage): F[PhotoEdAppState]
  def hide(appState: PhotoEdAppState): F[PhotoEdAppState]
  def close(appState: PhotoEdAppState): F[PhotoEdAppState]
}
