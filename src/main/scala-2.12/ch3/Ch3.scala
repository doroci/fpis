package ch3

/**
  * Created by lee on 2017. 1. 19..
  */
object Ch3 extends App{


  sealed trait List[+A] // sealed: 내부 파일에서만 사용 가능 (외부 파일에서 사용 못함)
  case object Nil extends List[Nothing]
  case class Cons[+A](head: A, tail: List[A]) extends List[A]

  object List {

    def sum(ints: List[Int]): Int = ints match {
      case Nil => 0
      case Cons(x,xs) => x + sum(xs)
    }

    def product(ds: List[Double]): Double = ds match {
      case Nil => 1.0
      case Cons(0.0, _) => 0.0
    }

    def apply[A](as: A*): List[A] =
      if(as.isEmpty) Nil
      else Cons(as.head, apply(as.tail: _*))

    val x = List (1,2,3,4,5) match {

      case Cons(x, Cons(2, Cons(4, _))) => x
      case Nil => 42
      case Cons(x, Cons(y, Cons(3, Cons(4, _ )))) => x + y  // 1 + 2
      case Cons(h, t) => h + sum(t)
      case _ => 10

    }

    def tail[A](l: List[A]): List[A] =
      l match {
        case Nil => sys.error("tail of empty list")
        case Cons(_, t) => t
      }

  }

  println(s" List.x, ${List.x}")
  println(s" List.tail(List(1,3,5)), ${List.tail(List(1,3,5))}")
  println(s" List.tail(List((1,2),(2,3))), ${List.tail(List((1,2),(2,3)))}")
  println(s" List.tail(List(1)), ${List.tail(List(1))}")

}



