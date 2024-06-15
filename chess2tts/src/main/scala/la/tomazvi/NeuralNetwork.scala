package la.tomazvi

import breeze.linalg._
import breeze.numerics._


object NeuralNetwork {
  def forward(
    x: DenseMatrix[Double],
    w1: DenseMatrix[Double]
  ): (DenseMatrix[Double], DenseMatrix[Double]) = {
    val h: DenseMatrix[Double] = x * w1
    val y = sigmoid(h)
    (h, y)
  }
}
