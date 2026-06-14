package com.pwroblew.photoed.lib.actions

import cats.MonadThrow
import cats.effect.std.Console
import com.pwroblew.photoed.lib.actions.transformations.simple.{Grayscale, Invert}
import com.pwroblew.photoed.lib.{EdImageLoader, EdImageViewer}

object EditorActions {
  def actions[F[_]: {MonadThrow,
    Console}](
      imageLoader: EdImageLoader[F],
      imageViewer: EdImageViewer[F]
  ): Map[String, EditorAction[F]]      = Map(
    "load"      -> new LoadAction[F](imageLoader),
    "load-res"  -> new LoadResAction[F](imageLoader),
    "save"      -> new SaveAction[F](imageLoader),
    "save-res"  -> new SaveResAction[F](imageLoader),
    "exit"      -> new ExitAction[F],
    "invert"    -> new TransformAction[F](Invert),
    "grayscale" -> new TransformAction[F](Grayscale),
    "status"    -> new StatusAction[F],
    "clear"     -> new ClearAction[F],
    "show"      -> new ShowAction[F](imageViewer),
    "hide"      -> new HideAction[F](imageViewer)
  )

}
