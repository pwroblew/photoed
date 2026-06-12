package com.pwroblew.photoed.lib

import java.awt.image.BufferedImage
import java.nio.file.Path

trait ImageFiles[F[_]] {
  def load(path: Path): F[BufferedImage]

  def save(image: BufferedImage, path: Path): F[Unit]
}
