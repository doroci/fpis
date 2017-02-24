

# parallelism

### 병렬성
```
val (l,r) = splitAt(ints.legnth / 2)
val sumL: Par[Int] = Par.unit(sum(l))
val sumR: Par[Int] = Par.unit(sum(r))
Par.get(sumL) + Par.get(sumR)

1. 반으로 나눠 병렬로 계산을 한다.
2. 두 결과를 추출해서 합한다.
```
* 문제점 1
```
평가를 동시에 해야하는데 그러면 get을 할때 참조 투명성이 깨질수 있다.
```
* 문제해결 - 병렬성 제거
```
Par.get(par.unit(sum(l))) +Par.get(par.unit(sum(r)))
```
* 문제점 2
```
 get으로 넘겨주는 즉시 get으로 완료까지 실행이 차단되는 부수 효과 발생.
 즉, 비동기적으로 계산들을 완료를 기다리지 않고도 조합을 할 수 있어야한다.
```

### 병렬 계산의 조합
* 엄격하게 유지하되 그 실행이 즉시 시작않되게 하면 ??
```
sum(IndexedSeq(1,2,3,4))
map2(
    sum(IndexedSeq(1,2)),
    sum(IndexedSeq(3,4)))(_ + _)
)
map2(
    map2(
        sum(IndexedSeq(1)),
        sum(IndexedSeq(2)))(_ + _),
    sum(IndexedSeq(3,4)))(_ + _)
    )
)
map2(
    map2(
        unit(1),
        unit(2))(_ + _),
    sum(IndexedSeq(3,4)))(_ + _)
    )
)
map2(
    map2(
        unit(1),
        unit(2)),(_ + _),
    map2(
        sum(IndexedSeq(3)),
        sum(IndexedSeq(4)))(_ + _))(_ + _)
...

```

* 문제점 1
```
엄격하게 서술을 하면 객체가 상당히 무거워 질 것이다.
map2를 게으르게 만들고 양변을 병렬로 즉시 실행하는 것이 낫다.
```

## 명시적 분기
* map2의 두 인수를 병렬로 평가하는 것이 바람직하지 않을까?
```
Par.map2(Par.unit(1), Par.unit(1))(_ + _)

두 계산은 빠르게 완료 될 것이며 따라서 굳이 개별적인 논리적 스레드를 띄울 필요가 없게된다.
```
* 문제점 1
```
현재의 API는 계산을 주 스레드로부터 분기하는 시점에 관해 그리 명료하지 않다.
즉, 프로그래머는 그러한 분기(forking)가 일어나는 지점 또는 시점을 구체적으로 지정 할 수 없다.
```

* 분기를 좀 더 명시 - 개별 논리적 스레드 에서 실행
```
Par.map2(Par.fork(sum(l))), Par.fork(map(r)))(_ + _)
```
* 문제점 2
```
fork같은 함수는 병렬 계산들을 너무 엄격하게 인스턴스화하는 문제를 해결해 주나
좀 더 근본적으로는 병렬성을 명시적으로 프로그래머의 통제하에 두는 역할을 한다.

명시적 분기에 관심사는 두 병렬 task의 결과들이 조합되어야 함을 지정하는 수단이 필요함과
별개적으로 특정 과제를 비동기적으로 수행할지 아닐지를 선택하는 수단도 필요하다.
```
* 비엄격함수 적용
```
def unit[A](a: A): Par[A]
def lazyUnit[A](a: => A): Par[A] = fork(unit(a))
```

* 문제점
```
for는 인수들을 개별 논리적 스레드에서 평가되게 하는 수단인데,
그러한 평가자가 호출 즉시 일어나게 할 것인지,
아니면 get같은 어떤 함수에 의해 계산이 강제될 떄까지 개별 논리적 스레드에서의 평가를 미룰 것인지는 아직 결정하지 않았다.
즉 평가가 fork의 책임인지, get의 책임인지의 문제를 결정해야 한다.
```
* 해결방안 팁
```
여러 의미를 가진 fork와 get의 구현에 어떤 정보가 필요한가를 생각해 보는 것이다.
구현이 무엇을 언제 사용할 것인지를 프로그래머가 좀 더 세밀하게 제어할 수 있다면 더 좋은 것이다.
```

* 인수의 평가를 변경
```
fork가 인수의 평가를 뒤로 미루게 하면
평가되지 않은 Par 인수를 받고 그 인수에 동시적 평가가 필요하다는 점을 표시만 해 두면 된다.

def run[A](a: Par[A]): A

```


## 7.3 - API의 경련


* 연습문제 7.3

* 연습문제 7.4

### 기존의 조합기로 표현 하기

* Par[List[Int]]의 결과가 정렬된 Par[List[Int]]로 변환
```
def sortPar(parList: Par[List[Int]): Par[List[Int]]
```

* parList를 map2의 양변 중 하나에 지정한다면 List의 내부에 접근해서 목록을 정렬
```
def sortPar(parList: Par[List[Int]]): Par[List[Int]] =
   map2(parList, unit(()) )( (a, _) => a.sorted)

```

* A => B 형식의 임의의 함수를, Par[A]를 받고 Par[B]를 돌려주는 함수로 승급

```
def map[A,B](pa>: Par[A])(f: A => B): Par[B] =
 map2(pa, unit(()) )( (a,_) => f(a))
```

```
def sortPar(parList: Par[List[Int]]) = map(parList)(_.sorted)
```

* 하나의 목록에 map을 병렬로 적용
```
def parMap[A,B](ps: List[A])(f: A => B): Par[List[B]]
```

* N개의 병렬 계산을 수월하게 분기하기
* asyncF가 병렬 계산 하나를 분기해서 결과를 산춤함으로써 A => B를 A => Par[B]로 변환
```
def parMap[A,B](ps: List[A])(f: A => B): Par[List[B]] = {
  val fbs: List[Par[B]] = ps.map(asyn(f))

  ...
}
```

* 연습문제 7.5

* 연습문제 7.6


## 7.5 - 조합기들을 가장 일반적인 형태로 정련

```
함수적 설계는 반복적인 과정이다. API의 명세를 만들고 적어도 하나의 prototype을 구현을 작성했다면
그것을 점점 복잡한 또는 현신ㄹ적인 시나리오에 사용해 봐야 한다.
그런데 바로 조합기를 구현해보기 보다는 그 조합기를 가장 일반적인 형태로정련할 수 있는지 살펴 보는 것이 바람직하다.
```

* 두 분기 계산 중 하나를 초기 계산의 결과에 기초해서 선택하는 함수
```
def choice[A](cond: Par[Boolean])(t: Par[A], f: Par[A]): Par[A]
```
```
이 함수는 만일 cond의 결과가 true이면 t를 사용해서 계산을 진행하고 cond의 결과가 false이면 f를 사용해서 계산을 진행한다.
```

* 결과를 이용해서 t나 f의 실행을 결정하는 식
```
def choice[A](cond: Par[Boolean])(t: Par[A], f: Par[A]): Par[A] =
  es =>
      if (run(es)(cond).get) t(es)
      else f(es)
```
```
여기서 boolean을 사용하는 것은 다소 자의적이다. 그리고 가능한 두 병렬 계산 t와 f중 하나를 선택하는 것도 사실 자의적이다.
```

* N개의 계산 중 하나를 선택
```
def choiceN[A](n: Par[Int])(choices: List[Par[A]): Par[A]
```

* 연습문제 7.13

* 연습문제 7.14





