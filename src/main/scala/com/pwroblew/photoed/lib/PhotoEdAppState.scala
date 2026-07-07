package com.pwroblew.photoed.lib

import com.pwroblew.photoed.lib.impl_io.EdImageJPanel

import javax.swing.JFrame

case class PhotoEdAppState(
    history: List[String],
    edImage: Option[EdImage],
    isShowing: Boolean,
    toBeContinued: Boolean,
    toBeShowed: Boolean
)

object PhotoEdAppState {
  private val empty: PhotoEdAppState = PhotoEdAppState(
    history = List.empty,
    edImage = Option.empty,
    isShowing = false,
    toBeContinued = true,
    toBeShowed = false
  )
  def initialState: PhotoEdAppState  = empty
}
