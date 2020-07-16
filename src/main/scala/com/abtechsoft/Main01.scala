package com.abtechsoft
import cats.effect.{IO, Timer}

import scala.concurrent.duration._
import fs2._

import scala.concurrent.ExecutionContext
/*
Proper resource allocation and closing
Pull-based communication between publishers and consumers
Easy reasoning about side effects

One can create pure stream using Stream.apply much like the List constructor
Stream can be "compiled" to another data structure
 */
object Main01 extends App {

  //pure-stream
  val pureStream: Stream[Pure, Int] = Stream(1, 2, 3, 4)
  val list = pureStream.compile.toList
  println(list)
  val a = Stream("Abc", "def")
  val b = Stream("ghi", "jkl")
  val c = (a ++ b).compile.toVector
  println(c)
  //effect-full stream

  def getAge(name: String): IO[Int] = IO(name.length)
  val stream = Stream("A", "B", "C")
  val effectFulStream = stream.evalMap(getAge)
  effectFulStream.compile.toList
    .unsafeRunSync() //side-effect
  effectFulStream.evalMap(name => IO(println(s"Received name $name"))): Stream[
    IO,
    Unit
  ] //It is not right way because it is transforming stream into Stream of Unit
  effectFulStream.evalTap(name => IO(println(s"Received name $name"))): Stream[
    IO,
    Int
  ] //Doesn't transform the stream and it execute passed effect
  effectFulStream.compile.drain //You don't care about the data but you do care about the execution of side-effect and it wouldn't give you any data back
  effectFulStream.compile.drain.unsafeRunSync() //Return is Unit

  stream.evalFilter(name => IO(name.contains("A")))

  //Infinite stream
  val infiniteStream = Stream.constant("Ping")
  val `20Items` = infiniteStream.take(20).compile.toList
  println(`20Items`)
  val repeat = Stream(1, 2, 3).repeat.take(20).compile.toList
  println(repeat)
  //Stream by time

  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)
  val listTimers =
    Stream.awakeDelay[IO](2.seconds).take(2).compile.toList.unsafeRunSync()
  println(listTimers)

  //File processing
}
