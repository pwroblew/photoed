package com.pwroblew.photoed.lib.actions.action_definitions.transformations.simple

import com.pwroblew.photoed.lib.actions.ActionKeyword
import com.pwroblew.photoed.lib.actions.ActionKeyword.INVERT

object Invert extends SimpleTransformation {

  override def description: String = "inverted"

  override def pixelTransform(value: Int): Int = {

    val pixel: Pixel = Pixel(value)

    val newR: Int = 255 - pixel.getR
    val newG: Int = 255 - pixel.getG
    val newB: Int = 255 - pixel.getB

    Pixel.create(newR, newG, newB).value

  }

  override def keywords: List[ActionKeyword] = List(INVERT)

  override def help: String =
    s"""invert: inverts the colors of the image
       |syntax: invert <id>""".stripMargin
}
