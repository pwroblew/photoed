package com.pwroblew.photoed.lib.actions.transformations.simple

object Grayscale extends SimpleTransformation {

  override def description: String = "grayscaled"

  override def pixelTransform(value: Int): Int = {
    val pixel: Pixel = Pixel(value)
    val grayed: Int  = (pixel.getR + pixel.getG + pixel.getB) / 3
    Pixel.create(grayed, grayed, grayed).value
  }

  override def keywords: List[String] = List("greyscale", "grayscale")
}
