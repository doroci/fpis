
val rng = new scala.util.Random

def rollDie: Double = {
  val rng = new scala.util.Random
  rng.nextInt
}

def rollDie1(rng: scala.util.Random): Int = rng.nextInt(6)

trait RNG {
  def nextInt: (Int, RNG)
}


case class SimpleRNG(seed: Long) extends RNG {
  def nextInt: (Int, RNG) = {
    val newSeed = (seed * 0x5DEECE66DL +0xBL) & 0xFFFFFFFFFFFL
    val nextRNG = SimpleRNG(newSeed)
    val n = (newSeed >>> 16).toInt
    (n, nextRNG)
  }
}

val simpleRng = SimpleRNG(32)
val (n1, rng2) = simpleRng.nextInt
val (n2, rng3) = rng2.nextInt


//class Foo {
//  private var s: FooState =   ???
//  def bar: Bar
//  def baz: Int
//}
//
//trait Foo2 {
//  def bar: (Bar, Foo)
//  def baz: (Int, Foo)
//}

def randomPair(rng: RNG): (Int, Int) = {
  val (i1,_) = rng.nextInt
  val (i2,_) = rng.nextInt
  (i1,i2)
}

def randomPair2(rng: RNG): ((Int,Int), RNG) = {
  val (i1, rng2) = rng.nextInt
  val (i2, rng3) = rng.nextInt
  ((i1,i2), rng3)
}
