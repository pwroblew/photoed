package com.pwroblew.photoed.lib

trait ImageViewer[F[_]] {
  def show(image: Image): F[Unit]
}
