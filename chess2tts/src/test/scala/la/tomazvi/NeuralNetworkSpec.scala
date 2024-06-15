package la.tomazvi

import breeze.linalg._
import breeze.numerics._
import org.scalatest._
import flatspec._
import matchers._
import scala.io.{ Source => IOSource }
import java.io.PrintWriter
import java.io.File


class NeuralNetworkSpec extends AnyFlatSpec with should.Matchers {
  /*
   * ┌─────┐┌───┐     ┌───┐   ┌───┐   ┌───────┐
   * │ a b ││ x │     │ a │   │ b │   │ax + by│
   * │ c d ││ y │ =  x│ c │+ y│ d │ = │cx + dy│
   * └─────┘└───┘     └───┘   └───┘   └───────┘
   * f(Wx) 
   * n of W collumns must be equal to the n of x rows
   * A: m x n => W: 2 x 3
   * B: n x p => x: 3 x 1
   * AB: m x p => Wx: 2 x 1
   */

  val m = DenseMatrix((1.0, 2.0), (4.0, 5.0))

  it should "move ones matrix forward" in {
    val (_ ,y)  = NeuralNetwork.forward(m, m)
    val expected = DenseMatrix(
      (0.9998766054240137, 0.9999938558253978), 
      (0.9999999999622486, 0.9999999999999953)
    )

    y shouldBe expected
  }

  it should "calculate loss gradient" in {
    val test = DenseMatrix(
      (1.0, 1.0),
      (1.0, 1.0)
    )

    val res = NeuralNetwork.lossGradient(m, test)
    val expected = DenseMatrix(
      (0.0, 2.0),
      (6.0, 8.0)
    )

    res shouldBe expected
  }
}
