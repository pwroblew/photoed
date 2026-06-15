package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.std.Console
import com.pwroblew.photoed.lib.actions.transformations.simple.{Grayscale, Invert}
import com.pwroblew.photoed.lib.{EdImageFiles, EdImageViewer}

object EditorActions {
  def actions[F[_]: {MonadThrow,
    Console}](
      imageLoader: EdImageFiles[F],
      imageViewer: EdImageViewer[F]
  ): Map[String, EditorAction[F]]      = Map(
    "load"      -> new LoadAction[F](imageLoader, imageViewer),
    "load-res"  -> new LoadResAction[F](imageLoader, imageViewer),
    "save"      -> new SaveAction[F](imageLoader),
    "save-res"  -> new SaveResAction[F](imageLoader),
    "exit"      -> new ExitAction[F](imageViewer),
    "invert"    -> new TransformAction[F](Invert, imageViewer),
    "grayscale" -> new TransformAction[F](Grayscale, imageViewer),
    "status"    -> new StatusAction[F],
    "clear"     -> new ClearAction[F](imageViewer),
    "show"      -> new ShowAction[F](imageViewer),
    "hide"      -> new HideAction[F](imageViewer)
  )

}
