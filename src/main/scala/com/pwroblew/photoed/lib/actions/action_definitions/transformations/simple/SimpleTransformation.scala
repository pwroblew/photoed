package com.pwroblew.photoed.lib.actions.action_definitions.transformations.simple

import com.pwroblew.photoed.lib.Image
import com.pwroblew.photoed.lib.actions.action_definitions.transformations.EdImageTransformation

import java.awt.image.BufferedImage

trait SimpleTransformation extends EdImageTransformation {

  override def transform(image: Image): Image = {
    val buffImage: BufferedImage = image.buffImage
    val width: Int               = image.width
    val height: Int              = image.height

    val newPixels: Array[Int] = Array.fill(width * height)(0)
    buffImage.getRGB(0, 0, width, height, newPixels, 0, width)
    newPixels.mapInPlace(pixelTransform)

    val newBuffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    newBuffImage.setRGB(0, 0, width, height, newPixels, 0, width)
    Image.fromBuffered(newBuffImage)
  }

  def pixelTransform(pixel: Int): Int

}
