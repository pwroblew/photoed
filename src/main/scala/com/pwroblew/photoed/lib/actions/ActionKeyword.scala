package com.pwroblew.photoed.lib.actions

import scala.util.Try

enum ActionKeyword:
  case CLEAR, CLEAR_RAW, CLOSE, LOAD, LOAD_RES, SAVE, SAVE_RES, EXIT, EXIT_RAW, INVERT, GRAYSCALE,
    GREYSCALE, STATUS, HISTORY,
    SHOW, DISPLAY, HIDE
  def toCmd: String = this.toString.toLowerCase

object ActionKeyword:
  def fromCmd(cmd: String): Option[ActionKeyword] =
    Try(ActionKeyword.valueOf(cmd.toUpperCase)).toOption
