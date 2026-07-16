package com.pwroblew.photoed.lib

import cats.effect.Ref

trait PhotoEdApp[F[_]] {
  def nextStep(
      appState: Ref[F, PhotoEdAppState],
      maybeImageViewer: Option[EdImageViewer[F]]
  ): F[Unit]
  def readCommand(appState: Ref[F, PhotoEdAppState]): F[Unit]
}
