package ch2

/**
  * Created by lee on 2017. 1. 12..
  */
object Ch2 {

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

  def main(args: Array[String]) {

    println(factorial(6))
    println(fib(6))

  }
}
