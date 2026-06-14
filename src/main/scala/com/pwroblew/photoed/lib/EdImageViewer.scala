package com.pwroblew.photoed.lib

import com.pwroblew.photoed.lib.impl_io.EdImageJPanel

import javax.swing.JFrame

trait EdImageViewer[F[_]] {
  def show(appState: PhotoEdAppState)(edImage: EdImage): F[PhotoEdAppState]
  def close(appState: PhotoEdAppState): F[PhotoEdAppState]
}
