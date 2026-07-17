package com.pwroblew.photoed.lib.actions

case class AdditionalActions(preActions: List[String], postActions: List[String])

object AdditionalActions {
  def empty: AdditionalActions =
    AdditionalActions(List.empty, List.empty)
}
