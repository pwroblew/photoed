package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.Resource
import cats.effect.std.Console
import com.pwroblew.photoed.lib.actions.transformations.simple.{Grayscale, Invert}
import com.pwroblew.photoed.lib.{EdImageFiles, EdImageViewer}

object EditorActions {
  def basicActions[F[_]: {MonadThrow,
    Console}](imageLoader: EdImageFiles[F]): Map[String, EditorActionBasic[F]]      = Map(
    "load"      -> new LoadAction[F](imageLoader),
    "load-res"  -> new LoadResAction[F](imageLoader),
    "save"      -> new SaveAction[F](imageLoader),
    "save-res"  -> new SaveResAction[F](imageLoader),
    "exit"      -> new ExitAction[F](),
    "invert"    -> new TransformAction[F](Invert),
    "grayscale" -> new TransformAction[F](Grayscale),
    "status"    -> new StatusAction[F],
    "clear"     -> new ClearAction[F]()
  )

  def showingActions[F[_]: {MonadThrow, Console}]: Map[String, EditorActionShowable[F]] = Map(
    "show" -> new ShowAction[F](),
    "hide" -> new HideAction[F]()
  )

  def allActions[F[_]: {MonadThrow,
    Console}](imageLoader: EdImageFiles[F]): Map[String, EditorActionShowable[F]] =
    basicActions(imageLoader) ++ showingActions

}
