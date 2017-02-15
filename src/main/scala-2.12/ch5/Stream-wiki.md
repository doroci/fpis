# Stream

### strictness - 엄격성
* argument를 항상 evaluate 한다.
```

def multiply(v1: BigDecimal) : BigDecimal = v1 * v1

val result = multiply(1.2)

[출력결과] result: BigDecimal = 1.44

```
<br>

### non-strictness - 비 엄격성
* argument 중 하나 이상을 evaluate 하지 않는다.
```
val ns1 = false && { println("not evaluate"); true}
[출력결과] ns1: false

val ns2 = true || { println("not evaluate"); false}
[출력결과] ns2: true

ns1과 ns2의 출력에서 "not evaluate"가 출력 되지 않았는데, 이것이 비 엄격성을 나타낸다.
```

* thunk(성크)는 표현식의 평가 되지 않는 형태를 말한다.
* () => A은 Function0[A] 과 동일하다.
```
def thunk[A](c: Boolean, isTrue: () => A, isFalse: () => A): A =
    if (c) isTrue() else isFalse ()

val t = thunk(false, () => println("is True"), () => println("is False"))

[출력결과] t: is False

* 스칼라가 아래와 같은 표현할 수 있다.
isTrue: => A, isFalse: => A
```

* 스칼라는 argument evaluate 결과를 캐싱하지 않는다. (기본적으로는)
```
def nonLazy(c: Boolean, i: => Int) = if (c) i+i else 0

val nl = nonLazy(true, {println("non-Lazy");10+10 })

[출력결과] nl:   non-Lazy
               non-Lazy
               Int = 40


def lazy(c: Boolean, i: => int) = {
    lazy val i2 = i
    if (c) i2 +i2 else 0
}

lazy(true, {println("lazy"); 10+10 })

[출력결과] nl:   non-Lazy
               Int = 40

```
<br>

## Lazy vs non-strict - 용어 구분
* https://wiki.haskell.org/Lazy_vs._non-strict
* https://en.wikipedia.org/wiki/Evaluation_strategy
<br>

##  Memoizing streams and avoiding recomputation - 스트림의 메모화를 통한 재계산 피하기
```

val x = Cons(() => expensive(x), tl)
val h1 = x.headOption
val h2 = x.headOption

위와 같은 코드느는 Cons생성자를 직접 사용 할 경우 expensive(x)가 두번 호출된다.
이를 일종의 디자인패턴(Cons를 직접 사용하는게 아니라 cons라는 함수를 만들어 호출, 이를 smart생성자라고 불른다고 한다.)으로 피할 수 있다.

def cons[A](hd: => A, tl: => Stream(A)): Stream[A] = {
    lazy val head = hd
    lazy val tail = tl
    Cons(() => head, () => tail)
}

def apply[A](as: A*): Stream[A] =
   if (as.isEmpty) empty
   else cons(as.head, apply(as.tail: _*))

결과적으로, as.head와 apply(as.tail: _*) 표현식은 Stream을 강제할 때까지는 평가되지 않는다.

```
<br>

## separation of concerns - 관심사의 분리
* 계산의 서술(description)을 그 계산의 실제 실행과 분리하는 것을 권장한다.
```
예를들어 일급 함수는 일부 계산을 자신의 본문에 담고 있으나, 그 계산은 오직 인수들이 전달되어야 실행한다.
또한, Option은 오류가 발행했다는 사실을 담고 있을 뿐, 오류에 대해 무엇을 수행할 것인가는 그와는 분리된 관심사이다.
즉, 나태성을 통해서 표현식의 서술을 그 표현식의 평가와 분리 할 수 있다.
```
<br>

* incremental - 점진성
```
Stream의 요소드을 다른 어떤 계산이 참조하는 시점이 되어서야 그 Stream을 생성하는 계산이 실제로 진행된다.
그리고 그때가 되면 구현은 요청된 요소들을 생성하는데 필요한 작업만 수행한다.
즉, 중간결과를 인스턴스화 하지 않고도 함수들을 연이어 호출이 가능하다.
```
<br>


## INFINITE STREAMS AND CORECURSION - 무한 스트림과 공재귀

```
    val ones: Stream[Int] = Stream.cons(1, ones)

    ones.tkae(5).toList
    res0: List[Int] = List(1 ,1 ,1 ,1 ,1)

    ones.exists( _ % 2 != 0)
    res1: Boolean = true

    1. ones.map(_ + 1).exists(_ % 2 == 0)
    2. ones.takeWhile(_ == 1)
    3. ones.forAll(_ != 1)

    위의 1,2,3의 결과는 같으나 평가의 과정은 다른 경우다.
```
<br>

![stream](/src/main/scala-2.12/ch5/image/stream.png)

## summary
```
    비엄격성은 표현식의 서술과 표현식의 평가 방법 및 시기를 분리함으로써 모듈성을 증가시킨다. 이처럼 관심사들을 분리하면, 필요에 따라 표현식의
    서로 다른 부분을 평가해서 서로 다른 결과를 얻는 식으로 표현식의 서술을 여러 문맥에서 재사용할 수 있다.
```


