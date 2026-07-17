package com.pwroblew.photoed.lib.actions.action_definitions

import cats.MonadThrow
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState
import com.pwroblew.photoed.lib.actions.ActionKeyword.CLOSE
import com.pwroblew.photoed.lib.actions.{ActionKeyword, AdditionalActions, EditorActionBasic}

class CloseAction[F[_]: {MonadThrow, Console}] extends EditorActionBasic[F] {

  override def actB(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] = {

    val maybeId: Option[String] = commandDetails.tail.headOption

    stateRef.update { state =>
      val imgId: String = maybeId.getOrElse(state.imagesStatus.head.id)

      state.copy(imagesStatus =
        state.imagesStatus.map(status =>
          if status.id == imgId then status.copy(toBeShown = false) else status
        )
      )
    } >> AdditionalActions.empty.pure[F]
  }

  override def keywords: List[ActionKeyword] = List(CLOSE)
}

object CloseAction {
  def apply[F[_]: {MonadThrow, Console}]: CloseAction[F] = new CloseAction[F]()
}
