package com.pwroblew.photoed.lib.actions.action_definitions

import cats.MonadThrow
import cats.data.StateT
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.StatefulCLI.MakeImageWindowResource
import com.pwroblew.photoed.lib.{ImageFileMgmnt, PhotoEdAppState}
import com.pwroblew.photoed.lib.actions.ActionKeyword.{HELP, STATUS}
import com.pwroblew.photoed.lib.actions.{
  ActionKeyword,
  AdditionalActions,
  EditorActionBasic,
  EditorActionShowable,
  EditorActions
}
import com.pwroblew.photoed.lib.impl_f.{WindowsManager, WindowsMap}

class HelpAction[F[_]: {Console, MonadThrow}](using
    imageLoader: ImageFileMgmnt[F],
    makeImageWindowResource: MakeImageWindowResource[F]
) extends EditorActionBasic[F] {

  override def actB(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] = {

    val actionsList: String = EditorActions.allActionsMap.keys
      .filter(_.available)
      .map(_.keyword)
      .mkString("[", ", ", "]")

    Console[F].println("To proceed, provide a command with its parameters.")
      >> Console[F].println("For further assistance, type: <command> help")
      >> Console[F].println(s"List of available commands: $actionsList")
      >> AdditionalActions.empty.pure[F]

  }

  override def keywords: List[ActionKeyword] = List(HELP)

  override protected def helpB: F[AdditionalActions] =
    Console[F].println("help: prints the general help message :)")
      >> Console[F].println("syntax: help")
      >> AdditionalActions.empty.pure
}
