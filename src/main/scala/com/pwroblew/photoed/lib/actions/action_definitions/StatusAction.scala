package com.pwroblew.photoed.lib.actions.action_definitions

import cats.MonadThrow
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState
import com.pwroblew.photoed.lib.actions.ActionKeyword.STATUS
import com.pwroblew.photoed.lib.actions.{ActionKeyword, AdditionalActions, EditorActionBasic}

class StatusAction[F[_]: {Console, MonadThrow}] extends EditorActionBasic[F] {

  private val indent: String = " " * 4

  override def actB(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] =
    for {
      state       <- stateRef.get
      statusString = state.imagesStatus.map { status =>
                       s"img-id:[${status.id}]  |  img-loaded:[YES]"
                     }.mkString("\n")
      _           <- Console[F].println(statusString)
    } yield AdditionalActions.empty

  override def keywords: List[ActionKeyword] = List(STATUS)
}
