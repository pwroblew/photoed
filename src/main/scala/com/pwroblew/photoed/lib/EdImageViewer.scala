package com.pwroblew.photoed.lib

import cats.effect.Ref

trait EdImageViewer[F[_]] {
  def show(appState: Ref[F, PhotoEdAppState[F]])(edImage: EdImage): F[Unit]
  def hide(appState: Ref[F, PhotoEdAppState[F]]): F[Unit]
  def close(appState: Ref[F, PhotoEdAppState[F]]): F[Unit]
}
