package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.data.EitherT
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState

class StatusAction[F[_]: {Console, MonadThrow}] extends EditorActionBasic[F] {

  private val indent: String = " " * 4

  override def actB(
      state: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] =
    for {
      st          <- state.get
      statusString = s"img-id:[${st.imageId}]  |  img-loaded:[${st.edImage.fold("NO")(_ => "YES")}]"
      _           <- Console[F].println(statusString)
    } yield AdditionalActions.empty

  override def keywords: List[String] = List("status")
}
