package com.pwroblew.photoed.lib.actions.action_definitions

import cats.data.OptionT
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import cats.{ApplicativeError, MonadThrow}
import com.pwroblew.photoed.lib.actions.ActionKeyword.{SAVE, SAVE_RES}
import com.pwroblew.photoed.lib.actions.action_definitions.SaveAction.saveImage
import com.pwroblew.photoed.lib.actions.{ActionKeyword, AdditionalActions, EditorActionBasic}
import com.pwroblew.photoed.lib.{Image, ImageFileMgmnt, PhotoEdAppState}

class SaveAction[F[_]: {MonadThrow, Console}](imageLoader: ImageFileMgmnt[F])
    extends EditorActionBasic[F] {

  override def actB(
      state: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String]
  ): F[AdditionalActions] = {

    if commandDetails.length != 3 then {
      val exception = new IllegalArgumentException(s"syntax: ${SAVE.toCmd} <img-id> <filename>")
      exception.raiseError[F, AdditionalActions]
    } else {

      val cmd: String      = commandDetails.head
      val imageId: String  = commandDetails(1)
      val pathBase: String = commandDetails(2)

      val maybeKeyword: Option[ActionKeyword] = ActionKeyword.fromCmd(cmd)

      val pathOrError: Either[Throwable, String] = maybeKeyword match {
        case Some(SAVE)     => pathBase.asRight
        case Some(SAVE_RES) => s"src/main/resources/$pathBase".asRight
        case _              => new RuntimeException(s"FATAL_ERROR in save action").asLeft
      }

      for {
        path <- ApplicativeError[F, Throwable].fromEither(pathOrError)
        _    <- saveImage(imageLoader.save)(state, path)
      } yield AdditionalActions.empty

    }

  }

  override def keywords: List[ActionKeyword] = List(SAVE, SAVE_RES)

  override protected def helpB: F[AdditionalActions] =
    Console[F].println("save: saves the image to a disk file with provided path/filename")
      >> Console[F].println("syntax: save <img-id> <filename>")
      >> AdditionalActions.empty.pure
}

object SaveAction {

  def apply[F[_]: {MonadThrow, Console}](using imageLoader: ImageFileMgmnt[F]): SaveAction[F] =
    new SaveAction(imageLoader)

  def saveImage[F[_]: MonadThrow](imageSaver: (Image, String) => F[Unit])(
      appState: Ref[F, PhotoEdAppState[F]],
      path: String
  ): F[Unit] = {

    val res: OptionT[F, Unit] = for {
      image <- OptionT(appState.get.map(state => state.imagesStatuses.headOption.map(_.image)))
      path  <- OptionT.pure[F](path)
      _     <- OptionT.liftF(imageSaver(image, path))
      _     <- OptionT.liftF(appState.update(state =>
                 state.copy(
                   toBeContinued = true
                 )
               ))
    } yield ()

    res.getOrRaise(
      new IllegalArgumentException(
        s"Invalid arguments for saving the image. Path: [$path] or image not loaded."
      )
    )
  }

}
