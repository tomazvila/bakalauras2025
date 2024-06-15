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

  def lossGradient(y: DenseMatrix[Double], tgt: DenseMatrix[Double]): DenseMatrix[Double] = {
    val diff = y - tgt
    diff.map(_ * 2)
  }

  def sigmoidGradient(x: DenseMatrix[Double], dY: DenseMatrix[Double]): DenseMatrix[Double] = {
    val y = sigmoid(x)
    val ones = DenseMatrix.ones[Double](y.rows, y.cols)
    dY *:* y *:* (ones - y)
  }
}
