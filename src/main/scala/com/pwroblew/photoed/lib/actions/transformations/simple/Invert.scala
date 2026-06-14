package com.pwroblew.photoed.lib.actions.transformations.simple

object Invert extends SimpleTransformation {

  override def description: String = "inverted"

  override def pixelTransform(value: Int): Int = {

    val pixel: Pixel = Pixel(value)

    val newR: Int = 255 - pixel.getR
    val newG: Int = 255 - pixel.getG
    val newB: Int = 255 - pixel.getB

    Pixel.create(newR, newG, newB).value

  }
}
