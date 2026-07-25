package com.pwroblew.photoed.lib.actions

enum ActionKeyword(val keyword: String, val available: Boolean) {
  case CLEAR     extends ActionKeyword("clear", true)
  case CLEAR_RAW extends ActionKeyword("clear-raw", false)
  case CLOSE     extends ActionKeyword("close", true)
  case LOAD      extends ActionKeyword("load", true)
  case LOAD_RES  extends ActionKeyword("load-res", false)
  case SAVE      extends ActionKeyword("save", true)
  case SAVE_RES  extends ActionKeyword("save-res", false)
  case EXIT      extends ActionKeyword("exit", true)
  case EXIT_RAW  extends ActionKeyword("exit-raw", false)
  case INVERT    extends ActionKeyword("invert", true)
  case GRAYSCALE extends ActionKeyword("grayscale", true)
  case GREYSCALE extends ActionKeyword("greyscale", true)
  case STATUS    extends ActionKeyword("status", true)
  case HISTORY   extends ActionKeyword("history", true)
  case HELP      extends ActionKeyword("help", true)
  case SHOW      extends ActionKeyword("show", true)
  case DISPLAY   extends ActionKeyword("display", false)
  case HIDE      extends ActionKeyword("hide", true)

  def toCmd: String = this.keyword
}

object ActionKeyword:
  def fromCmd(cmd: String): Option[ActionKeyword] = {
    ActionKeyword.values.find(_.keyword == cmd)
  }
