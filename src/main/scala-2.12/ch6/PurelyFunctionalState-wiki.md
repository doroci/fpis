# Purely Functional State

### State Updates - 상태갱신
* 상태갱신은 부수효과로서 참조에 투명하지 않는다.
* 참조 투명성이 낮을수록 상대적으로 테스트, 합성 모듈화, 병렬화에 어렵다.
```
* scala.util.Random 함수를 사용하여 상태갱신 구현
* 무작위성을 활용하는 메서드는 reproducibility(재현성)에 대한 테스트가 필요하다.

def rollDie1: Int = {
  val rng = new scala.util.Random
  rng.nextInt(10)
}

def rollDie2(rng: scala.util.Random): Int = rng.nextInt(10)

* rooDie1메서드는 0~9의 값을 반환하므로 10중 1번은 실패를 하게된다.
* rooDie2메서드는 nextInt함수를 호출할때마다 이전의 rng의 상태가 파괴된다.
```


* A functional RNG - 함수적 난수 발생기
![pureFunctionRNG](/src/main/scala-2.12/ch6/image/pureFunctionRNG.png)
```
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

* simpleRng -> nextInt -> rng2 -> nextInt -> rng3

```

* stateful API to pure - 상태있는 api를 순수하게
```
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

* rng.nextInt -> rng2
* rng.nextInt -> rng3
* ((i1,i2), rng3) -> rng

```

