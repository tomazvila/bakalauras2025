package la.tomazvi

import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.Timers
import breeze.linalg._
import la.tomazvi.IrisReaderActor._
import la.tomazvi.IrisNNActor._
import scala.io.Source
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

object IrisReaderActor {
  sealed trait Command
  final case class ReadIrisXData(replyTo: ActorRef[IrisXData]) extends Command
  final case class ReadIrisYData(replyTo: ActorRef[IrisYData]) extends Command
  
  final case class IrisXData(dta: DenseMatrix[Double])
  final case class IrisYData(tgt: DenseMatrix[Int])

  def apply(): Behavior[Command] =
    Behaviors.receive[Command] { (context, message) => message match {
      case ReadIrisXData(replyTo) => 
        val data = readData("iris_x.txt")
        val dta = parseIrisX(data)
        replyTo ! IrisXData(dta)
        Behaviors.same
      case ReadIrisYData(replyTo) => 
        val data = readData("iris_y.txt")
        val tgt = parseIrisY(data)
        replyTo ! IrisYData(tgt)
        Behaviors.same
    }
  }

  private def readData(fileName: String): List[String] = {
    val source = Source.fromResource(fileName)
    try {
      source.getLines.toList
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

object IrisNNActor {
  sealed trait Command
  final object Train extends Command

  private final case class IrisXDataAdapter(response: IrisReaderActor.IrisXData) extends Command
  private final case class IrisYDataAdapter(response: IrisReaderActor.IrisYData) extends Command

  final case class IrisDataContainer(
    dta: Option[DenseMatrix[Double]],
    tgt: Option[DenseMatrix[Int]]
  ) {
    def hasBoth: Boolean = dta.isDefined && tgt.isDefined 
  }

  def apply(): Behavior[Command] = Behaviors.setup { context =>
    val reader = context.spawn(IrisReaderActor(), "Iris_reader_actor")

    val xDataAdapter: ActorRef[IrisReaderActor.IrisXData] = context.messageAdapter(response => IrisXDataAdapter(response))
    val yDataAdapter: ActorRef[IrisReaderActor.IrisYData] = context.messageAdapter(response => IrisYDataAdapter(response))

    Behaviors.receiveMessage[Command] {
      case Train =>
        reader ! ReadIrisXData(xDataAdapter)
        reader ! ReadIrisYData(yDataAdapter)

        readData(IrisDataContainer(None, None))
    }
  }

  def readData(datacontainer: IrisDataContainer): Behavior[Command] = Behaviors.receiveMessage[Command] {
    case IrisXDataAdapter(IrisXData(dta)) =>
      val updated = datacontainer.copy(dta = Some(dta))
      if (updated.hasBoth) {
        train(updated)
      } else {
        readData(updated)
      }
    case IrisYDataAdapter(IrisYData(tgt)) =>
      val updated = datacontainer.copy(tgt = Some(tgt))
      if (updated.hasBoth) {
        train(updated)
      } else {
        readData(updated)
      }
  }
}

object PekkoQuickstart extends App {
  val irisNNActor: ActorSystem[IrisNNActor.Command] = ActorSystem(IrisNNActor(), "Iris_Neural_Network_Actor")
  irisNNActor ! Train
}
