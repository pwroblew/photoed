package com.pwroblew.photoed.lib.actions.transformations.simple

class InvertSpec extends munit.FunSuite {
  test("inverting black pixel") {
    val result: Int = Invert.pixelTransform(Pixel.BLACK.value)
    assertEquals(Pixel(result), Pixel.WHITE)
  }

  test("inverting red pixel") {
    val result: Int = Invert.pixelTransform(Pixel.RED.value)
    assertEquals(Pixel(result), Pixel.CYAN)
  }

  test("inverting yellow pixel") {
    val result: Int = Invert.pixelTransform(Pixel.YELLOW.value)
    assertEquals(Pixel(result), Pixel.BLUE)
  }
}
