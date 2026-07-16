package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.std.Console
import com.pwroblew.photoed.lib.EdImageFiles
import com.pwroblew.photoed.lib.actions.transformations.simple.{Grayscale, Invert}

object EditorActions {

  def basicActions2[F[_]: {MonadThrow, Console}](using
      imageLoader: EdImageFiles[F]
  ): List[EditorActionBasic[F]] = List(
    ClearAction[F],
    CloseAction[F],
    LoadAction[F],
    LoadResAction[F],
    SaveAction[F],
    SaveResAction[F],
    ExitAction[F],
    TransformAction[F](Invert),
    TransformAction[F](Grayscale),
    StatusAction[F]
  )

  def showingActions2[F[_]: {MonadThrow, Console}]: List[EditorActionShowable[F]] = List(
    DisplayAction[F],
    ShowAction[F],
    HideAction[F]
  )

  def actionsMap[F[_], EdAction[G[_]] <: EditorActionShowable[G]](actions: List[EdAction[F]])
      : Map[String, EdAction[F]] = actions
    .map(action => (action, action.keywords))
    .flatMap((a, keys) => keys.map(_ -> a))
    .toMap

  def allActionsMap[F[_]: {MonadThrow,
    Console}](using imageLoader: EdImageFiles[F]): Map[String, EditorActionShowable[F]] =
    actionsMap(basicActions2[F]) ++ actionsMap(showingActions2)

  def basicActionsMap[F[_]: {MonadThrow,
    Console}](using imageLoader: EdImageFiles[F]): Map[String, EditorActionBasic[F]] =
    actionsMap(basicActions2[F])

}
