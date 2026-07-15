package com.pwroblew.photoed.lib

import cats.effect.Ref

trait PhotoEdApp[F[_]] {
  def nextStep(
      command: String,
      appState: Ref[F, PhotoEdAppState],
      maybeImageViewer: Option[EdImageViewer[F]]
  ): F[Unit]
  def readCommand(): F[String]
}
