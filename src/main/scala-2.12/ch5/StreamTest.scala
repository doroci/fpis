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

}
