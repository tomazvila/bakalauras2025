package la.tomazvi

import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import breeze.linalg.DenseMatrix
import la.tomazvi.IrisReaderActor._
import scala.io.Source


object IrisReaderActor {
  sealed trait Command
  final object ReadIrisXData extends Command
  final object ReadIrisYData extends Command
  
  sealed trait Response
  final case class IrisXData(dta: DenseMatrx[Double]) extends Response
  final case class IrisYData(tgt: DenseMatrx[Int]) extends Response

  def apply(): Behavior[Command] =
    Behaviors.receiveMessage[Command] { 
      case ReadIrisXData => 
        val data = readData("iris_x.txt")
        val dta = parseIrisX(data)
        Behaviors.same
      case ReadIrisYData =>
        val data = readData("iris_y.txt")
        val tgt = parseIrisY(data)
        Behaviors.same
    }

  private def readData(fileName: String): List[String] = {
    val source = Source.fromResource(fileName)
    try {
      val lines: List[String] = source.getLines.toList
      lines
    } finally {
      source.close()
    }
  }

  private def parseIrisX(lines: List[String]): DenseMatrix[Double] = {
    DenseMatrix(lines.map(_.split(" ").map(_.toDouble)): _*)
  }

  private def parseIrisY(lines: List[String]): DenseMatrix[Int] = {
    DenseMatrix(lines.map(_.split(" ").map(_.toInt)): _*)
  }
}

object PekkoQuickstart extends App {
  val irisReadActor: ActorSystem[IrisReaderActor.Command] = ActorSystem(IrisReaderActor(), "Iris_reader_actor")

  irisReadActor ! ReadIrisXData  
  irisReadActor ! ReadIrisYData  
}
