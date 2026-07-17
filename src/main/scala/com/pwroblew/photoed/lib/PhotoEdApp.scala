package com.pwroblew.photoed.lib

import cats.effect.Ref
import com.pwroblew.photoed.lib.impl_f.WindowsManager

trait PhotoEdApp[F[_]] {
  def nextStep(stateRef: Ref[F, PhotoEdAppState[F]], windowsManager: WindowsManager[F]): F[Unit]
  def readCommand(stateRef: Ref[F, PhotoEdAppState[F]]): F[Unit]
}
