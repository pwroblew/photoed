package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.Resource
import cats.effect.std.Console
import com.pwroblew.photoed.lib.actions.action_definitions.transformations.simple._
import com.pwroblew.photoed.lib.actions.action_definitions._
import com.pwroblew.photoed.lib.{ImageFileMgmnt, ImageWindow}
import com.pwroblew.photoed.lib.actions.action_definitions.transformations.simple.Invert

object EditorActions {

  def basicActions[F[_]: {MonadThrow, Console}](using
      imageLoader: ImageFileMgmnt[F]
  ): List[EditorActionBasic[F]] = List(
    ClearAction[F],
    CloseAction[F],
    LoadAction[F],
    SaveAction[F],
    ExitAction[F],
    TransformAction[F](Invert),
    TransformAction[F](Grayscale),
    StatusAction[F],
    HistoryAction[F]
  )

  def showingActions[F[_]: {MonadThrow, Console}](using
      makeImageWindowResource: String => Resource[F, ImageWindow[F]]
  ): List[EditorActionShowable[F]] = List(
    DisplayAction[F],
    ShowAction[F],
    HideAction[F]
  )

  def actionsMap[F[_], EdAction[G[_]] <: EditorActionShowable[G]](actions: List[EdAction[F]])
      : Map[ActionKeyword, EdAction[F]] = actions
    .map(action => (action, action.keywords))
    .flatMap((a, keys) => keys.map(_ -> a))
    .toMap

  def allActionsMap[F[_]: {MonadThrow, Console}](using
      imageLoader: ImageFileMgmnt[F],
      makeImageWindowResource: String => Resource[F, ImageWindow[F]]
  ): Map[ActionKeyword, EditorActionShowable[F]] =
    actionsMap(basicActions[F]) ++ actionsMap(showingActions[F])

  def basicActionsMap[F[_]: {MonadThrow, Console}](using
      imageLoader: ImageFileMgmnt[F]
  ): Map[ActionKeyword, EditorActionBasic[F]] =
    actionsMap(basicActions[F])

}
