package com.pwroblew.photoed.lib.impl_io

import cats.effect.IO
import com.pwroblew.photoed.lib.actions.transformations.simple.Pixel
import com.pwroblew.photoed.lib.{EdImage, EdImageFiles}
import munit.CatsEffectSuite

import java.awt.image.BufferedImage
import java.io.File

class EdImageFilesSpec extends CatsEffectSuite {

  val loader: EdImageFiles[IO] = EdImageFilesImpl

  test("save and load png image") {
    val buffImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB)
    buffImage.setRGB(0, 0, Pixel.BLUE.value)
    buffImage.setRGB(0, 1, Pixel.RED.value)
    buffImage.setRGB(1, 0, Pixel.GREEN.value)
    buffImage.setRGB(1, 1, Pixel.WHITE.value)

    val tmpImageFile: File = File.createTempFile("photoed-test", ".png")

    for {
      _           <- loader.save(EdImage.fromBuffered(buffImage), tmpImageFile.getAbsolutePath)
      loadedImage <- loader.load(tmpImageFile.getAbsolutePath)
      _           <- IO {
                       assert(tmpImageFile.exists())
                       assert(tmpImageFile.length() > 0)
                       assertEquals(loadedImage.width, 2)
                       assertEquals(loadedImage.height, 2)
                       assertEquals(loadedImage.getImage.getRGB(0, 0), Pixel.BLUE.value)
                       assertEquals(loadedImage.getImage.getRGB(0, 1), Pixel.RED.value)
                       assertEquals(loadedImage.getImage.getRGB(1, 0), Pixel.GREEN.value)
                       assertEquals(loadedImage.getImage.getRGB(1, 1), Pixel.WHITE.value)
                     }
      _           <- IO(tmpImageFile.delete())
    } yield ()
  }

  test("save and load jpg image") {
    val buffImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB)
    buffImage.setRGB(0, 0, Pixel.BLUE.value)
    buffImage.setRGB(0, 1, Pixel.RED.value)
    buffImage.setRGB(1, 0, Pixel.GREEN.value)
    buffImage.setRGB(1, 1, Pixel.WHITE.value)

    val tmpImageFile: File = File.createTempFile("photoed-test", ".jpg")

    for {
      _           <- loader.save(EdImage.fromBuffered(buffImage), tmpImageFile.getAbsolutePath)
      loadedImage <- loader.load(tmpImageFile.getAbsolutePath)
      _           <- IO {
                       assert(tmpImageFile.exists())
                       assert(tmpImageFile.length() > 0)
                       assertEquals(loadedImage.width, 2)
                       assertEquals(loadedImage.height, 2)
                     }
      _           <- IO(tmpImageFile.delete())
    } yield ()
  }

  test("load and test real png image") {

    val path: String = getClass.getResource("/red100x100.png").getPath

    for {
      loadedImage <- loader.load(path)
      _           <- IO {
                       assertEquals(loadedImage.width, 100)
                       assertEquals(loadedImage.height, 100)
                       assertEquals(loadedImage.getImage.getRGB(50, 50), Pixel.RED.value)
                     }
    } yield ()
  }

}
