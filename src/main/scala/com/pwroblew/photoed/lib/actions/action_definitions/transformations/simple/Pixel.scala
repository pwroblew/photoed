package com.pwroblew.photoed.lib.actions.action_definitions.transformations.simple

case class Pixel(value: Int) extends AnyVal {
  def getA: Int = (value & 0xff000000) >> 24
  def getR: Int = (value & 0x00ff0000) >> 16
  def getG: Int = (value & 0x0000ff00) >> 8
  def getB: Int = value & 0x000000ff
}

object Pixel {
  def create(r: Int, g: Int, b: Int): Pixel = {
    new Pixel((255 << 24) | (r << 16) | (g << 8) | b)
  }

  val BLACK: Pixel      = Pixel.create(0, 0, 0)
  val WHITE: Pixel      = Pixel.create(255, 255, 255)
  val RED: Pixel        = Pixel.create(255, 0, 0)
  val CYAN: Pixel       = Pixel.create(0, 255, 255)
  val YELLOW: Pixel     = Pixel.create(255, 255, 0)
  val BLUE: Pixel       = Pixel.create(0, 0, 255)
  val GRAY_DARK: Pixel  = Pixel.create(85, 85, 85)
  val GREEN: Pixel      = Pixel.create(0, 255, 0)
  val MAGENTA: Pixel    = Pixel.create(255, 0, 255)
  val GRAY_LIGHT: Pixel = Pixel.create(170, 170, 170)
}
