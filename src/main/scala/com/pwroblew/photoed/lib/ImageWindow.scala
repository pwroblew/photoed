package com.pwroblew.photoed.lib

import cats.effect.Ref

trait ImageWindow[F[_]] {
  def display(edImage: Image): F[Unit]
  def hide: F[Unit]
  def close: F[Unit]
  def isBeingShown: F[Boolean]
}
