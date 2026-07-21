package com.pwroblew.photoed.lib.impl_f

import cats.data.{OptionT, StateT}
import cats.effect.kernel.{Concurrent, MonadCancel}
import cats.effect.std.Semaphore
import cats.effect.{Ref, Resource, Sync}
import cats.syntax.all.*
import com.pwroblew.photoed.lib.{Image, ImageWindow}

type WindowsMap[F[_]] = Map[String, ImageWindowResource[F]]

final class WindowsManager[F[_]: {[G[_]] =>> MonadCancel[G, Throwable], Concurrent}] {

  private val mutexF: F[Semaphore[F]] = Semaphore[F](1)

  def open(
      id: String,
      makeImageWindowResource: String => Resource[F, ImageWindow[F]]
  ): StateT[F, WindowsMap[F], Unit] = StateT {
    windowsMap =>
      for {
        mutex             <- mutexF
        _                 <- mutex.acquire
        (winMap, unitEff) <- windowsMap.get(id) match {
                               case Some(window) => (windowsMap, ()).pure[F]
                               case None         => for {
                                   (imageWindow, release) <- makeImageWindowResource(id).allocated
                                   imageWindowResource     = ImageWindowResource(imageWindow, release)
                                 } yield (windowsMap + (id -> imageWindowResource), ())
                             }
        _                 <- mutex.release
      } yield (winMap, unitEff)
  }

  def close(id: String): StateT[F, WindowsMap[F], Unit] = StateT { windowsMap =>
    for {
      mutex             <- mutexF
      _                 <- mutex.acquire
      (winMap, unitEff) <- windowsMap.get(id) match {
                             case None         => (windowsMap, ()).pure[F]
                             case Some(window) =>
                               window.releaseEffect >> (windowsMap - id, ()).pure[F]
                           }
      _                 <- mutex.release
    } yield (winMap, unitEff)
  }

  def closeAll(): StateT[F, WindowsMap[F], Unit] = StateT { windowsMap =>
    for {
      mutex             <- mutexF
      _                 <- mutex.acquire
      (winMap, unitEff) <-
        windowsMap.toList.map(_._2).traverse(_.releaseEffect).void >> (
          Map.empty[String, ImageWindowResource[F]],
          ()
        ).pure[F]
      _                 <- mutex.release
    } yield (winMap, unitEff)
  }

  def display(id: String, image: Image): StateT[F, WindowsMap[F], Unit] = StateT { windowsMap =>
    for {
      mutex          <- mutexF
      _              <- mutex.acquire
      maybeDisplayed <- windowsMap
                          .get(id)
                          .map(_.imageWindow)
                          .traverse(_.display(image))
      _              <- mutex.release
    } yield (windowsMap, maybeDisplayed.void)
  }

  def hide(id: String): StateT[F, WindowsMap[F], Unit] = StateT { windowsMap =>
    for {
      mutex       <- mutexF
      _           <- mutex.acquire
      maybeHidden <- windowsMap
                       .get(id)
                       .map(_.imageWindow)
                       .traverse(_.hide)
      _           <- mutex.release
    } yield (windowsMap, maybeHidden.void)
  }

}

object WindowsManager {
  def makeResource[F[_]: {[G[_]] =>> MonadCancel[G, Throwable],
    Concurrent}]: Resource[F, WindowsManager[F]] =
    Resource.make[F, WindowsManager[F]] { new WindowsManager[F].pure[F] } { windowsManager =>
      windowsManager.closeAll().runF.void
    }
}
