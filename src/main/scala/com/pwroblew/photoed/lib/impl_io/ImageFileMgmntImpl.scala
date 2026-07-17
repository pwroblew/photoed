package com.pwroblew.photoed.lib.impl_io

import cats.effect.IO
import cats.implicits.catsSyntaxEq
import cats.syntax.all.*
import com.pwroblew.photoed.lib.{Image, ImageFileMgmnt}

import java.awt.image.BufferedImage
import java.awt.{Color, Graphics2D}
import java.io.File
import javax.imageio.ImageIO
import scala.util.matching.Regex

object ImageFileMgmntImpl extends ImageFileMgmnt[IO] {

  private val FilenameRegex: Regex = raw".*\.([a-zA-Z]{2,4})".r

  override def load(path: String): IO[Image] =
    IO.blocking(ImageIO.read(new File(path)))
      .map(toIntArgb)
      .map(Image.fromBuffered)

  override def save(edImage: Image, path: String): IO[Unit] = {

    val (fullPath, extension) = path match {
      case FilenameRegex(ext) => (path, ext)
      case _                  => (path + ".png", "png")
    }

    for {
      buffImageToBeSaved <- IO.fromEither(prepareForWriting(edImage.buffImage, extension))
      _                  <- IO.blocking {
                              val file: File   = new File(fullPath).getAbsoluteFile
                              val res: Boolean = ImageIO.write(buffImageToBeSaved, extension, file)

                              if (!res)
                                throw new RuntimeException(s"No ImageIO writer found for $extension.")

                              if (!file.exists() || file.length() == 0)
                                throw new RuntimeException(s"File was not written: ${file.getAbsolutePath}")
                            }
    } yield ()

  }

  private def prepareForWriting(
      buffImage: BufferedImage,
      extension: String
  ): Either[Throwable, BufferedImage] =

    extension.toLowerCase match {

      case "jpg" | "jpeg" =>
        toIntRgb(buffImage).asRight

      case "png" | "gif" =>
        buffImage.asRight

      case other =>
        new IllegalArgumentException(s"Unsupported format: $other").asLeft
    }

  private def toIntArgb(src: BufferedImage): BufferedImage = {
    val targetType: Int = BufferedImage.TYPE_INT_ARGB

    if src.getType === targetType then src
    else {
      val dst = new BufferedImage(src.getWidth, src.getHeight, targetType)

      val g: Graphics2D = dst.createGraphics()
      try g.drawImage(src, 0, 0, null)
      finally g.dispose()

      dst
    }
  }

  private def toIntRgb(src: BufferedImage): BufferedImage = {
    val targetType: Int = BufferedImage.TYPE_INT_RGB

    if src.getType === targetType then src
    else {
      val dst = new BufferedImage(src.getWidth, src.getHeight, targetType)

      val g: Graphics2D = dst.createGraphics()
      try {
        g.setColor(Color.WHITE)
        g.fillRect(0, 0, dst.getWidth, dst.getHeight)
        g.drawImage(src, 0, 0, null)
      } finally {
        g.dispose()
      }

      dst
    }

  }
}
