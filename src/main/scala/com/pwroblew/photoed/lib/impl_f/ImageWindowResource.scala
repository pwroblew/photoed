package com.pwroblew.photoed.lib.impl_f

import com.pwroblew.photoed.lib.ImageWindow

final case class ImageWindowResource[F[_]](
    imageWindow: ImageWindow[F],
    releaseEffect: F[Unit]
)
