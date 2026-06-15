package com.pwroblew.photoed.lib

trait EdImageFiles[F[_]] {
  def load(path: String): F[EdImage]

  def save(edImage: EdImage, path: String): F[Unit]
}
