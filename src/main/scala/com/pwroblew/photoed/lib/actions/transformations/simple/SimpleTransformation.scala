package com.pwroblew.photoed.lib.actions.transformations.simple

import com.pwroblew.photoed.lib.EdImage
import com.pwroblew.photoed.lib.actions.transformations.EdImageTransformation

import java.awt.image.BufferedImage

trait SimpleTransformation extends EdImageTransformation {

  override def transform(image: EdImage): EdImage = {
    val buffImage: BufferedImage = image.buffImage
    val width: Int               = image.width
    val height: Int              = image.height

    val newPixels: Array[Int] = Array.fill(width * height)(0)
    buffImage.getRGB(0, 0, width, height, newPixels, 0, width)
    newPixels.mapInPlace(pixelTransform)

    val newBuffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    newBuffImage.setRGB(0, 0, width, height, newPixels, 0, width)
    EdImage.fromBuffered(newBuffImage)
  }

  def pixelTransform(pixel: Int): Int

}
