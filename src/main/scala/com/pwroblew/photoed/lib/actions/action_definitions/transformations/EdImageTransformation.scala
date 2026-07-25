package com.pwroblew.photoed.lib.actions.action_definitions.transformations

import com.pwroblew.photoed.lib.Image
import com.pwroblew.photoed.lib.actions.ActionKeyword

trait EdImageTransformation {
  def transform(image: Image): Image
  def description: String
  def keywords: List[ActionKeyword]
  def help: String
}
