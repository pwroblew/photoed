package com.pwroblew.photoed.lib

import com.pwroblew.photoed.lib.impl_io.EdImageJPanel

import javax.swing.JFrame

case class PhotoEdAppState(
    stateStatus: List[String],
    edImage: Option[EdImage],
    swingComponents: Option[(jFrame: JFrame, imageJPanel: EdImageJPanel)],
    isShowing: Boolean
)

object PhotoEdAppState {
  private val empty: PhotoEdAppState = PhotoEdAppState(
    stateStatus = List.empty,
    edImage = Option.empty,
    swingComponents = None,
    isShowing = false
  )
  def initialState: PhotoEdAppState  = empty
}
