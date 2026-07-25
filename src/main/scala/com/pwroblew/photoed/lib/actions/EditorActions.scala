package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.std.Console
import com.pwroblew.photoed.StatefulCLI.MakeImageWindowResource
import com.pwroblew.photoed.lib.ImageFileMgmnt
import com.pwroblew.photoed.lib.actions.action_definitions.*
import com.pwroblew.photoed.lib.actions.action_definitions.transformations.simple.*

object EditorActions {

  def basicActions[F[_]: {MonadThrow, Console}](using
      imageLoader: ImageFileMgmnt[F],
      makeImageWindowResource: MakeImageWindowResource[F]
  ): List[EditorActionBasic[F]] = List(
    ClearAction[F],
    LoadAction[F],
    SaveAction[F],
    ExitAction[F],
    TransformAction[F](Invert),
    TransformAction[F](Grayscale),
    HistoryAction[F],
    HelpAction[F]
  )

  def showingActions[F[_]: {MonadThrow, Console}](using
      makeImageWindowResource: MakeImageWindowResource[F]
  ): List[EditorActionShowable[F]] = List(
    DisplayAction[F],
    ShowAction[F],
    HideAction[F],
    CloseAction[F],
    StatusAction[F]
  )

  def actionsMap[F[_], EdAction[G[_]] <: EditorActionShowable[G]](actions: List[EdAction[F]])
      : Map[ActionKeyword, EdAction[F]] = actions
    .map(action => (action, action.keywords))
    .flatMap((a, keys) => keys.map(_ -> a))
    .toMap

  def allActionsMap[F[_]: {MonadThrow, Console}](using
      imageLoader: ImageFileMgmnt[F],
      makeImageWindowResource: MakeImageWindowResource[F]
  ): Map[ActionKeyword, EditorActionShowable[F]] =
    actionsMap(basicActions[F]) ++ actionsMap(showingActions[F])

  def basicActionsMap[F[_]: {MonadThrow, Console}](using
      imageLoader: ImageFileMgmnt[F],
      makeImageWindowResource: MakeImageWindowResource[F]
  ): Map[ActionKeyword, EditorActionBasic[F]] =
    actionsMap(basicActions[F])

}
