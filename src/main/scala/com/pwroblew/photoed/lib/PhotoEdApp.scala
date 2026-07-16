package com.pwroblew.photoed.lib

import cats.effect.Ref
import com.pwroblew.photoed.lib.impl_f.WindowHandle

trait PhotoEdApp[F[_]] {
  def nextStep(
      appState: Ref[F, PhotoEdAppState[F]],
      windowHandle: WindowHandle[F]
  ): F[Unit]
  def readCommand(appState: Ref[F, PhotoEdAppState[F]]): F[Unit]
}
