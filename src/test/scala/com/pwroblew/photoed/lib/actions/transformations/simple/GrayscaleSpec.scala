package com.pwroblew.photoed.lib.actions.transformations.simple

class GrayscaleSpec extends munit.FunSuite {
  test("graying black pixel") {
    val result: Int = Grayscale.pixelTransform(Pixel.BLACK.value)
    assertEquals(Pixel(result), Pixel.BLACK)
  }

  test("graying white pixel") {
    val result: Int = Grayscale.pixelTransform(Pixel.WHITE.value)
    assertEquals(Pixel(result), Pixel.WHITE)
  }

  test("graying red pixel") {
    val result: Int = Grayscale.pixelTransform(Pixel.RED.value)
    assertEquals(Pixel(result), Pixel.GRAY_DARK)
  }

  test("graying green pixel") {
    val result: Int = Grayscale.pixelTransform(Pixel.GREEN.value)
    assertEquals(Pixel(result), Pixel.GRAY_DARK)
  }

  test("graying blue pixel") {
    val result: Int = Grayscale.pixelTransform(Pixel.BLUE.value)
    assertEquals(Pixel(result), Pixel.GRAY_DARK)
  }

  test("graying cyan pixel") {
    val result: Int = Grayscale.pixelTransform(Pixel.CYAN.value)
    assertEquals(Pixel(result), Pixel.GRAY_LIGHT)
  }

  test("graying magenta pixel") {
    val result: Int = Grayscale.pixelTransform(Pixel.MAGENTA.value)
    assertEquals(Pixel(result), Pixel.GRAY_LIGHT)
  }

  test("graying yellow pixel") {
    val result: Int = Grayscale.pixelTransform(Pixel.YELLOW.value)
    assertEquals(Pixel(result), Pixel.GRAY_LIGHT)
  }

  test("graying gray_light pixel") {
    val result: Int = Grayscale.pixelTransform(Pixel.GRAY_LIGHT.value)
    assertEquals(Pixel(result), Pixel.GRAY_LIGHT)
  }

  test("graying gray_dark pixel") {
    val result: Int = Grayscale.pixelTransform(Pixel.GRAY_DARK.value)
    assertEquals(Pixel(result), Pixel.GRAY_DARK)
  }
}
