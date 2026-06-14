package com.pwroblew.photoed.lib

trait EdImageLoader[F[_]] {
  def load(path: String): F[EdImage]

  def save(edImage: EdImage, path: String): F[Unit]
}
