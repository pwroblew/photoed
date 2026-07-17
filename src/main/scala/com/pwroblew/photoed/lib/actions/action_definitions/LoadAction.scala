package com.pwroblew.photoed.lib.actions.action_definitions

import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import cats.{ApplicativeError, MonadThrow}
import com.pwroblew.photoed.lib.actions.ActionKeyword.{LOAD, LOAD_RES, SHOW}
import com.pwroblew.photoed.lib.actions.action_definitions.LoadAction.loadImage
import com.pwroblew.photoed.lib.actions.{ActionKeyword, AdditionalActions, EditorActionBasic}
import com.pwroblew.photoed.lib.{Image, ImageFileMgmnt, ImageStatus, PhotoEdAppState}

class LoadAction[F[_]: {MonadThrow, Console}](
    imageLoader: ImageFileMgmnt[F]
) extends EditorActionBasic[F] {

  override def actB(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] = {

    if commandDetails.length != 3 then {
      val exception = new IllegalArgumentException(s"syntax: ${LOAD.toCmd} <filename> <img-id>")
      exception.raiseError[F, AdditionalActions]
    } else {

      val cmd: String      = commandDetails.head
      val pathBase: String = commandDetails(1)
      val imageId: String  = commandDetails(2)

      val maybeKeyword: Option[ActionKeyword] = ActionKeyword.fromCmd(cmd)

      val pathOrError: Either[Throwable, String] = maybeKeyword match {
        case Some(LOAD)     => pathBase.asRight
        case Some(LOAD_RES) => s"src/main/resources/$pathBase".asRight
        case _              => new RuntimeException(s"FATAL_ERROR in load action").asLeft
      }

      for {
        path <- ApplicativeError[F, Throwable].fromEither(pathOrError)
        _    <- loadImage(imageLoader.load)(path, imageId)(stateRef)
      } yield AdditionalActions(List.empty, List(s"${SHOW.toCmd} ${imageId}"))
    }
  }

  override def keywords: List[ActionKeyword] = List(LOAD, LOAD_RES)
}

object LoadAction {
  def apply[F[_]: {MonadThrow, Console}](using imageLoader: ImageFileMgmnt[F]): LoadAction[F] =
    new LoadAction(imageLoader)

  def loadImage[F[_]: MonadThrow](edImageLoader: String => F[Image])(
      path: String,
      imageId: String
  )(appState: Ref[F, PhotoEdAppState[F]]): F[Unit] = {
    for {
      imageLoaded <- edImageLoader(path)
      _           <- appState.update(state =>
                       state.copy(
                         history = state.history :+ s"[loaded: $path]",
                         imagesStatus = state.imagesStatus :+ ImageStatus(imageId, imageLoaded, false, false)
                       )
                     )
    } yield ()
  }
}
