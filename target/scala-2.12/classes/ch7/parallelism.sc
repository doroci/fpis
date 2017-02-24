import java.util.concurrent.{ExecutorService, Future, TimeUnit}

import ch7.parallelism.Par.Par

val list = List(1,2,3,4,5)
list.head
list.tail

def sum(ints: Seq[Int]): Int =
  ints.foldLeft(0)((a,b) => a+b)

sum(Seq())


def sum2(ints: IndexedSeq[Int]): Int =
  if(ints.size <= 1)
    ints.headOption getOrElse 0
  else {
    val (l,r) = ints.splitAt(ints.length / 2)
    sum(l) + sum(r)
  }

sum2(IndexedSeq())

//
//def sum3(ints: IndexedSeq[Int]): Int =
//  if (ints.size <= 1)
//      ints.headOption getOrElse 0
//  else {
//    val (l,r) = ints.splitAt(ints.length / 2)
//    val sumL: Par[Int] = Par.unit(sum3(l))
//    val sumR: Par[Int] = Par.unit(sum3(r))
//    Par.get(sumL) + Par.get(sumR)
//  }

//def unit[A](a: => A): Par[A]
//def get[A](a: Par[A]): A
//
//def sum4(ints: IndexedSeq[Int]): Int =
//  if (ints.size <=1 )
//     Par.unit(ints.headOption getOrElse 0)
//  else {
//    val (l,r) = ints.splitAt(ints.length /2)
//    Par.map2(sum4(l), sum4(r))(_ + _)
//  }
//
//
//def map2[A,B,C](a: Par[A], b: Par[B])(f: (A,B) => C) : Par[C]
//
//
//sum(IndexedSeq(1,2,3,4)).map2(sum(IndexedSeq(1,2), sum(IndexedSeq(3,4,))(_ + _)


private case class UnitFuture[A](get: A) extends Future[A] {
  def isDone = true
  def get(timeout: Long, units: TimeUnit) = get
  def isCancelled = false
  def cancel(evenIfRunning: Boolean): Boolean = false
}

def map2[A,B,C](a: Par[A], b: Par[B])(f: (A,B) => C): Par[C] =
  (es: ExecutorService) => {
    val af = a(es)
    val bf = b(es)
    UnitFuture(f(af.get, bf.get))
  }







