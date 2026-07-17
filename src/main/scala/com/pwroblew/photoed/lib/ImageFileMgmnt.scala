package com.pwroblew.photoed.lib

trait ImageFileMgmnt[F[_]] {
  def load(path: String): F[Image]

  def save(edImage: Image, path: String): F[Unit]
}
