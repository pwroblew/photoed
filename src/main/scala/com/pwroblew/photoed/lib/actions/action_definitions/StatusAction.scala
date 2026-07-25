package com.pwroblew.photoed.lib.actions.action_definitions

import cats.MonadThrow
import cats.data.StateT
import cats.effect.Ref
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed.lib.PhotoEdAppState
import com.pwroblew.photoed.lib.actions.ActionKeyword.STATUS
import com.pwroblew.photoed.lib.actions.{ActionKeyword, AdditionalActions, EditorActionShowable}
import com.pwroblew.photoed.lib.impl_f.{WindowsManager, WindowsMap}

class StatusAction[F[_]: {Console, MonadThrow}] extends EditorActionShowable[F] {

  private val indent: String = " " * 4

  override def act(
      stateRef: Ref[F, PhotoEdAppState[F]],
      commandDetails: List[String],
      windowsManager: WindowsManager[F]
  ): StateT[F, WindowsMap[F], AdditionalActions] = {

    for {
      imageStatuses  <-
        StateT.liftF(stateRef.get.map(state =>
          state.imagesStatuses.map(imStatus => (imStatus.id, s"img-id:[${imStatus.id}]  |  img-loaded:[YES]"))
        ))
      windowStatuses <- windowsManager.status
      finalOutput     = {
        if !windowStatuses.forall(winStatus => imageStatuses.exists(imgStatus => imgStatus._1 == winStatus._1)) then
          "FATAL: there are windows opened for images not being loaded!"
        else if imageStatuses.isEmpty then "[no images loaded yet]"
        else {
          imageStatuses.map { (id, tempStatus) =>
            val maybeTuple: Option[(String, Boolean)] = windowStatuses.find(winStatus => winStatus._1 == id)
            maybeTuple.fold(s"$tempStatus  window:[NO]  being-shown:[NO]") { (_, beingShown) =>
              s"$tempStatus  |  window:[YES]  |  being-shown:[$beingShown]"
            }
          }.mkString("\n")
        }
      }
      _              <- StateT.liftF(Console[F].println(finalOutput))
    } yield {
      AdditionalActions.empty
    }

  }

  override def keywords: List[ActionKeyword] = List(STATUS)

  override protected def help: StateT[F, WindowsMap[F], AdditionalActions] =
    StateT.liftF(Console[F].println("status: prints the status of currently loaded images")
      >> Console[F].println("syntax: status")
      >> AdditionalActions.empty.pure)
}
