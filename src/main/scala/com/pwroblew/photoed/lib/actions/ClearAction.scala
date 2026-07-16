package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.{Ref, Resource}
import cats.effect.std.Console
import cats.syntax.all._
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

class ClearAction[F[_]: {MonadThrow, Console}] extends EditorActionBasic[F] {

  override def actB(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] = {
    val headCommand: String = commandDetails.head // TODO consider headOption
    headCommand match {
      case "clear"    => AdditionalActions(List("close", "clearRaw"), List.empty[String]).pure[F]
      case "clearRaw" => stateRef.update(state =>
          state.copy(
            edImage = Option.empty,
            isShowing = false,
            toBeContinued = true,
            toBeShown = false
          )
        )
          >> AdditionalActions.empty.pure[F]
    }
  }

  override def keywords: List[String] = List("clear", "clearRaw")
}

object ClearAction {
  def apply[F[_]: {MonadThrow, Console}]: ClearAction[F] = new ClearAction[F]()
}
