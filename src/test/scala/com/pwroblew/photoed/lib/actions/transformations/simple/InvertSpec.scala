package com.pwroblew.photoed.lib.actions.transformations.simple

class InvertSpec extends munit.FunSuite {
  test("inverting black pixel") {
    val result: Int = Invert.pixelTransform(Pixel.BLACK.value)
    assertEquals(Pixel(result), Pixel.WHITE)
  }
}
