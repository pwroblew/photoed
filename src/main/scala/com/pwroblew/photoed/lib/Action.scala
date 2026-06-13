package com.pwroblew.photoed.lib

import cats.effect.IO

trait Action[F[_]] {
  def run(state: PhotoAppState, commandDetails: List[String]): F[(Boolean, PhotoAppState)]
}
