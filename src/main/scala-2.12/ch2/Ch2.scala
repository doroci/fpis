package ch2

/**
  * Created by lee on 2017. 1. 12..
  */
object Ch2 extends App{

  //팩토리얼
  def factorial(n : Int) : Int = {
    @annotation.tailrec //컴파일러가 optimization를 해준다(stack을 소비하지 않도록 해준다.)
    def go(n : Int, acc : Int) : Int =
      if (n <= 0 ) acc
      else go(n-1, n * acc) //조건문에 go함수를 호출하여 loop와 동일한 역할을 한다.
    go(n,1)
  }

  //피보나치 수열
  def fib(n : Int ) : Int ={
    @annotation.tailrec
    def go(n: Int, prev: Int, cur: Int) : Int =
      if(n == 0) prev
      else go(n-1, cur, prev+cur)
    go(n,0,1)
  }

  //배열에서 문자열을 찾는 단항적 함수 (monomorphic function)
  def findFirst(ss: Array[String], key: String ) : Int = {
    @annotation.tailrec
    def loop(n: Int): Int = {
      if (n >= ss.length) -100
      else if (ss(n) == key) n
      else loop(n+1)
    }
    loop(0)
  }

  //배열에서 한 요소를 찾는 다형적 함수
  def findFirst[A](as: Array[A], p: A => Boolean): Int = {
    @annotation.tailrec
    def loop(n: Int): Int = {
      if(n >= as.length) -100
      else if (p(as(n))) n
      else loop(n+1)
    }
    loop(0)
  }

  //Array[A]가 주어진 비교 함수에 의거해서 정렬되어 있는지 점검하는 isSorted 함수를 구현
  def isSorted[A](as: Array[A], ordered: (A,A) => Boolean ): Boolean = {

    def go(n: Int): Boolean = {
      if(n >= as.length-1) true
      else if(ordered(as(n), as(n+1))) false
      else go(n+1)
    }
    go(0)
  }


    println(s" factorial(6), ${factorial(6)}")
    println(s" fib(6), ${fib(6)}")
    println(s" findFirst(Array('a','b','c','d', 'c'), ${findFirst(Array("'a'","'b'","'c'","'d'") ,"'c'")}")
    println(s" isSorted, ${isSorted(ordered = (x: Int, y:Int) => x > y , as = Array(1,2,3))}")
    println(s" isSorted, ${isSorted(ordered = (x: Int, y:Int) => x < y , as = Array(1,3,2))}")
}

