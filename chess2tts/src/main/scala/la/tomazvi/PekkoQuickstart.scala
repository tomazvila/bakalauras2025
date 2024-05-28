package la.tomazvi

import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import la.tomazvi.IrisReaderActor._
import scala.io.Source

object IrisReaderActor {
  sealed trait Command
  final object ReadIrisXData extends Command
  final object ReadIrisYData extends Command

  def apply(): Behavior[Command] =
    Behaviors.receiveMessage[Command] { 
      case ReadIrisXData => 
        readData("iris_x.txt")
        Behaviors.same
      case ReadIrisYData =>
        readData("iris_y.txt")
        Behaviors.same
    }

  private def readData(fileName: String): Unit = {
    val source = Source.fromResource(fileName)
    try {
      val lines = source.getLines.toList
      // Process the lines as needed
      lines.foreach(println)
    } finally {
      source.close()
    }
  }
}

object PekkoQuickstart extends App {
  val irisReadActor: ActorSystem[IrisReaderActor.Command] = ActorSystem(IrisReaderActor(), "Iris_reader_actor")

  irisReadActor ! ReadIrisXData  
  irisReadActor ! ReadIrisYData  
}
