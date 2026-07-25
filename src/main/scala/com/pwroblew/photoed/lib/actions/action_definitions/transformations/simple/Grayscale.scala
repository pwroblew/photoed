package com.pwroblew.photoed.lib.actions.action_definitions.transformations.simple

import com.pwroblew.photoed.lib.actions.ActionKeyword
import com.pwroblew.photoed.lib.actions.ActionKeyword.{GRAYSCALE, GREYSCALE}

object Grayscale extends SimpleTransformation {

  override def description: String = "grayscaled"

  override def pixelTransform(value: Int): Int = {
    val pixel: Pixel = Pixel(value)
    val grayed: Int  = (pixel.getR + pixel.getG + pixel.getB) / 3
    Pixel.create(grayed, grayed, grayed).value
  }

  override def keywords: List[ActionKeyword] = List(GRAYSCALE, GREYSCALE)

  override def help: String =
    s"""greyscale: converts the image to the greyscale
       |syntax: greyscale <id>""".stripMargin
}
