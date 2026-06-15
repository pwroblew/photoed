package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState
import com.pwroblew.photoed.lib.actions.EditorAction.emptyAction

trait EditorAction[F[_]: MonadThrow] {

  def act(state: PhotoEdAppState, commandDetails: List[String]): F[(Boolean, PhotoEdAppState)]

  def run(state: PhotoEdAppState, commandDetails: List[String]): F[(Boolean, PhotoEdAppState)] =
    for {
      (_, state1)     <- prev.run(state, commandDetails)
      (cont2, state2) <- act(state1, commandDetails)
      res             <- if (cont2) next.run(state2, commandDetails)
                         else (cont2, state2).pure[F]
    } yield res

  def next: EditorAction[F] = emptyAction
  def prev: EditorAction[F] = emptyAction

}

object EditorAction {
  def emptyAction[F[_]: MonadThrow]: EditorAction[F] = new EditorAction[F] {

    override def act(
        state: PhotoEdAppState,
        commandDetails: List[String]
    ): F[(Boolean, PhotoEdAppState)] =
      (true, state).pure[F]

    override def run(
        state: PhotoEdAppState,
        commandDetails: List[String]
    ): F[(Boolean, PhotoEdAppState)] =
      for {
        res <- act(state, commandDetails)
      } yield res

  }
}
