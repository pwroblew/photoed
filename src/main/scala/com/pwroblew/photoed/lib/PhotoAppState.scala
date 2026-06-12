package com.pwroblew.photoed.lib

case class PhotoAppState(
    imageDesc: Option[String],
    image: Option[Image]
)

object PhotoAppState {
  private val empty: PhotoAppState = PhotoAppState(
    imageDesc = Option.empty,
    image = Option.empty
  )
  def initialState: PhotoAppState  = empty
}
