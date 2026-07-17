package com.pwroblew.photoed.lib

import cats.effect.Ref

trait ImageWindow[F[_]] {
  def show(appState: Ref[F, PhotoEdAppState[F]])(edImage: Image): F[Unit]
  def hide(appState: Ref[F, PhotoEdAppState[F]]): F[Unit]
  def close(appState: Ref[F, PhotoEdAppState[F]]): F[Unit]
}
