# Stream

### strictness - 엄격성
* argument를 항상 evaluate 한다.
```

def multiply(v1: BigDecimal) : BigDecimal = v1 * v1

val result = multiply(1.2)

[출력결과] result: BigDecimal = 1.44

```


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



