package demo

import demo.game.GameDemo
import demo.increment.IncrementDemo
import demo.interop.IsThirteenDemo
import demo.shelter.ShelterDemo

object Main {
  def main(args: Array[String]): Unit = {
    IncrementDemo.mount()
    GameDemo.mount()
    ShelterDemo.mount()
    IsThirteenDemo.mount()
  }
}
