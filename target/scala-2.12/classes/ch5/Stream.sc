List(1,2,3,4).map(_ + 10).filter(_ % 2 == 0).map(_ * 3)
List(11,12,13,14).filter( _ % 2 == 0).map(_ * 3)
List(12,14).map(_ * 3)

//strictness: 엄격성
def sqaure(x: Double): Double = x * x
sqaure(41.0 + 1.0)


false && { println("!!"); true}

true || { println("!!"); false}

val result = if ("test".isEmpty) sys.error("empty input") else "test"

//val result2 = if ("".isEmpty) sys.error("empty input") else "test"



// () is thunk And () => A == Function0[A]
def if2[A](cond: Boolean, onTrue : () => A, onFalse: () => A): A =
    if (cond) onTrue() else onFalse()

val a = 10

if2( a < 22,
  () => println("a"),
  () => println("b")
)


def if3[A](cond: Boolean, onTrue: => A, onFalse: => A): A =
    if (cond) onTrue else onFalse

if3(false, sys.error("fail"), 3)


// i+i = i+(i), 스칼라의 + 연산자는 메서드를 호출하는것이다
def maybeTwice(b: Boolean, i: => Int) = if (b) i+i else 0
def maybeTwice2(b: Boolean, i: Int) = if (b) i+i else 0

maybeTwice(true,{println("hi"); 41+1})
maybeTwice2(true,{println("hi"); 41+1})


def maybeTwice3(b: Boolean, i: => Int) = {
   val j = i
//  val c = i
  if (b) j+j else 0
}

maybeTwice3(true, {println("hi"); 41+1})

def nonLazy(c: Boolean, i: => Int) = if (c) i+i else 0
val nl = nonLazy(true, {println("non-Lazy");10+10 })


def lazyEvaluate(c: Boolean, i: => Int) = {
  val i2 = i
  //  println(s"i2 value is ${i2}")
  if (c) i2 +i2 else 0
}

lazyEvaluate(true, {println("lazy"); 10+10 })


//5.2  게으른 목록
trait Stream[+A]{

  def toList: List[A] = {
    @annotation.tailrec
    def go(s: Stream[A], acc: List[A]): List[A] = s match {
      case Cons(h,t) => go(t(), h() :: acc)
      case _ => acc
    }
    go(this, List()).reverse
  }


  @annotation.tailrec
  final def drop(n: Int): Stream[A] = this match {
    case Cons(_, t) if n > 0 => t().drop(n - 1)
    case _ => this
  }



}




case object Empty extends Stream[Nothing]
case class Cons[+A](h: () => A, t: () => Stream[A]) extends Stream[A]

object Stream {

  def cons[A](hd: => A, tl: => Stream[A]): Stream[A] = {
    lazy val head = hd
    lazy val tail = tl
    Cons(() => head, () => tail)
  }

  def empty[A]: Stream[A] = Empty

  def apply[A](as: A*): Stream[A] =
    if (as.isEmpty) empty else cons(as.head, apply(as.tail: _*))

}





// exercise 5.2
def drop[A](s: Stream[A], n: Int): Stream[A] = s match {
  case Cons(_, t) if n > 0 => t().drop(n - 1)
  case _ => s
}

val ones: Stream[Int]  = Stream.cons(1, ones)



