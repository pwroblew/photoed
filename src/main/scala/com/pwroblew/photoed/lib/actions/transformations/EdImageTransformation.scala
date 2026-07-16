package com.pwroblew.photoed.lib.actions.transformations

import com.pwroblew.photoed.lib.EdImage

trait EdImageTransformation {
  def transform(image: EdImage): EdImage
  def description: String
  def keywords: List[String]
}
