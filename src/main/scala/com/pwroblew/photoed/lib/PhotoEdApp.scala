package com.pwroblew.photoed.lib

import cats.data.StateT
import cats.effect.Ref
import com.pwroblew.photoed.lib.impl_f.{WindowsManager, WindowsMap}

trait PhotoEdApp[F[_]] {
  def nextStep(
      stateRef: Ref[F, PhotoEdAppState[F]],
      windowsManager: WindowsManager[F]
  ): StateT[F, WindowsMap[F], Unit]
  def readCommand(stateRef: Ref[F, PhotoEdAppState[F]]): F[Unit]
}
