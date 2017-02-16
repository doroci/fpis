package ch5

/**
  * Created by lee on 2017. 2. 15..
  */
object StreamTest extends App{
  
  val list = List(1,2,3,4,5)
  val listToStream =list.toStream
  val range = 1 to 5
  val rangeToStream = range.toStream
  val stream = Stream(1,2,3,4,5)

  listToStream eq  stream
  listToStream == stream
  listToStream equals stream

  println(listToStream eq stream)
  println(listToStream == stream)
  println(listToStream equals stream)




  println("------------------------------------------")
  println(s"list has $list")
  println(s"listToStream has $listToStream")
  println(s"rangeToStream has $rangeToStream")
  println(s"range has $range")
  println(s"stream has $stream")



  import scala.math.BigInt
  val fibs: Stream[BigInt] = BigInt(0) #:: BigInt(1) #:: fibs.zip(fibs.tail).map { n => n._1 + n._2 }

  println("------------------------------------------")
  fibs take 5 foreach println
  println("------------------------------------------")

  val fibs2: Stream[BigInt] = BigInt(0) #:: BigInt(1) #:: fibs2.zip(
    fibs2.tail).map(n => {
    println("Adding %d and %d".format(n._1, n._2))
    n._1 + n._2
  })

  println("------------------------------------------")
  fibs2 take 5 foreach println
  println("------------------------------------------")
  fibs2 take 6 foreach println
  println("------------------------------------------")


}
