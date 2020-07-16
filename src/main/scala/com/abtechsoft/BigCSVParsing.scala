package com.abtechsoft

import java.io.File
import cats.implicits._
import cats.effect.{Blocker, ExitCode, IO, IOApp}
import io.circe.generic.auto._
import io.circe.syntax._
import fs2._

object BigCSVParsing extends IOApp {

  case class Row(code: String,
                 url: String,
                 creator: String,
                 createdAt: String,
                 lastModifiedAt: String,
                 productName: String,
                 genericName: String,
                 quantity: String)
  val dataset = new File("input.csv")
  val output = new File("output.json")
  override def run(args: List[String]): IO[ExitCode] = {
    Blocker[IO]
      .use(blocker => {
        fs2.io.file
          .readAll[IO](dataset.toPath, blocker, 4096)
          .through(text.utf8Decode)
          .through(text.lines)
          .map(str=>{
            println(str.split(",").toList)
            str.split(",")
          })
          .collect({
            case Array(
                code,
                url,
                creator,
                createdAt,
                lastModifiedAt,
                productName,
                genericName,
                quantity
                ) =>
              Row(
                code,
                url,
                creator,
                createdAt,
                lastModifiedAt,
                productName,
                genericName,
                quantity
              )
          })
          .map(_.asJson.noSpaces)
          .through(text.utf8Encode)
          .through(fs2.io.file.writeAll(output.toPath, blocker))
          .compile
          .drain >> IO(println("Done!"))
      })
      .as(ExitCode.Success)
  }
}
