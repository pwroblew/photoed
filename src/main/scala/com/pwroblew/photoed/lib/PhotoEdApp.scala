package com.pwroblew.photoed.lib

import cats.effect.Ref

trait PhotoEdApp[F[_]] {
  def basicStep(command: String, appState: Ref[F, PhotoEdAppState]): F[Unit]
  def showingStep(
      command: String,
      appState: Ref[F, PhotoEdAppState],
      imageViewer: EdImageViewer[F]
  ): F[Unit]
  def readCommand(): F[String]
}
