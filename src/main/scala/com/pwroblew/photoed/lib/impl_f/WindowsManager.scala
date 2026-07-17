package com.pwroblew.photoed.lib.impl_f

import cats.syntax.all.*
import cats.data.OptionT
import cats.effect.kernel.MonadCancel
import cats.effect.{Ref, Resource, Sync}
import com.pwroblew.photoed.lib.ImageWindow

final class WindowsManager[F[_]: [G[_]] =>> MonadCancel[G, Throwable]](
    val windowsRefs: Ref[F, Map[String, ImageWindowResource[F]]]
) {

  def open(id: String, res: Resource[F, ImageWindow[F]]): F[Unit] = {

    val computation: OptionT[F, Unit] = for {
      _                 <- OptionT(windowsRefs.get.map(refs => Option.when(!refs.keySet.contains(id))(())))
      (viewer, release) <- OptionT.liftF(res.allocated[ImageWindow[F]])
      _                 <- OptionT.liftF(windowsRefs.update(windows =>
                             windows + (id -> ImageWindowResource(viewer, release))
                           ))
    } yield ()
    computation.value.void
  }

  def close(id: String): F[Unit] =
    windowsRefs.modify { windows =>
      windows.get(id) match {
        case None         => windows        -> ().pure[F]
        case Some(window) => (windows - id) -> window.releaseEffect
      }
    }.flatten

  def closeAll(): F[Unit] = {
    windowsRefs.modify { windows =>
      windows.empty -> windows.toList.map(_._2).traverse(_.releaseEffect)
    }.flatten.void
  }

}

object WindowsManager {
  def makeResource[F[_]: Sync]: Resource[F, WindowsManager[F]] =
    Resource.make[F, WindowsManager[F]] {
      for {
        windowRef <-
          Ref.of[F, Map[String, ImageWindowResource[F]]](Map.empty[String, ImageWindowResource[F]])
      } yield new WindowsManager(windowRef)
    } { windowsManager =>
      windowsManager.closeAll()
    }
}
