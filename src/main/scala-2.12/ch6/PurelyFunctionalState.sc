
val rng = new scala.util.Random

def rollDie: Double = {
  val rng = new scala.util.Random
  rng.nextInt
}

def rollDie1(rng: scala.util.Random): Int = rng.nextInt(6)

trait RNG {
  def nextInt: (Int, RNG)
}

trait RNG_D {
  def nextDouble: (Double, RNG_D)
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



// y = RNG => (A, RNG)
// 어떠한 RNG에 의존하며, 그것을 이용하여 A를 생성하며,
// RNG를 다른 동작이 이후에 사용할 수 있는 새로운 상태로 전이하는 구현이다.

type Rand[+A] = RNG => (A, RNG)
type Rand2[A] = RNG => (A, RNG)

val int: Rand[Int] = _.nextInt
val int2: Rand2[Int] = _.nextInt

/*
// execute 1
n1: Int = 12311964
rng2: RNG = SimpleRNG(806876925355)
n2: Int = 72818735
rng3: RNG = SimpleRNG(4772248630234)


// execute 2
n1: Int = 12311964
rng2: RNG = SimpleRNG(806876925355)
n2: Int = 72818735
rng3: RNG = SimpleRNG(4772248630234)
 */


def unit[A](a: A): Rand[A] =
  rng => (a, rng)


def map[A,B](s: Rand[A])(f: A => B): Rand[B] =
  rng => {
    val (a, rng2) = s(rng)
    (f(a), rng2)
  }


// 0 <= x && x % 2 == 0
def nonNegativeEven: Rand[Int] =
    map(nonNegativeEven)(i => i - i % 2)

// exercise 6.1
def nonNegativeInt(rng: RNG): (Int, RNG) = {
  val (i, r) = rng.nextInt
  (if (i < 0) -(i + 1) else i, r)
}

// exercise 6.2
def double(rng: RNG): (Double, RNG) = {
  val (i, r) = nonNegativeInt(rng)
  (i / (Int.MaxValue.toDouble + 1), r)
}


// exercise 6.3
def intDouble(rng: RNG): ((Int, Double), RNG) = {
  val (i, r1) = rng.nextInt
  val (d, r2) = double(r1)
  ((i, d), r2)
}

def doubleInt(rng: RNG): ((Double, Int), RNG) = {
  val ((i, d), r) = intDouble(rng)
  ((d, i), r)
}

def double3(rng: RNG): ((Double, Double, Double), RNG) = {
  val (d1, r1) = double(rng)
  val (d2, r2) = double(r1)
  val (d3, r3) = double(r2)
  ((d1, d2, d3), r3)
}

// exercise 6.4
def ints(count: Int)(rng: RNG): (List[Int], RNG) =
  if (count == 0)
    (List(), rng)
  else {
    val (x, r1)  = rng.nextInt
    val (xs, r2) = ints(count - 1)(r1)
    (x :: xs, r2)
  }

// A tail-recursive solution
def ints2(count: Int)(rng: RNG): (List[Int], RNG) = {
  def go(count: Int, r: RNG, xs: List[Int]): (List[Int], RNG) =
    if (count == 0)
      (xs, r)
    else {
      val (x, r2) = r.nextInt
      go(count - 1, r2, x :: xs)
    }
  go(count, rng, List())
}


// exercise 6.5
val _double: Rand[Double] =
  map(nonNegativeInt)(_ / (Int.MaxValue.toDouble + 1))

// exercise 6.6
def map2[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A,B) => C): Rand[C] =
  rng => {
    val (a, r1) = ra(rng)
    val (b, r2) = rb(r1)
    (f(a, b), r2)
  }


