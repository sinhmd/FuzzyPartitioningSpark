package iet.unipi.bigdatamining.discretization.model

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
  * A class that represents the model of the Filter, namely the FuzzyPartitioning algorithm.
  * For each continuous feature the corresponding cut points generated by the FuzzyPartitioning algorithm are stored
  * Note: categorical features are not considered
  *
  * @param cores a map with the cut points (value) associated to each feature (key)
  * @author Armando Segatori
  */
// TODO: create methods for saving and loading the model
class FuzzyPartitioningModel(val cores: collection.Map[Int, List[Double]]) extends Serializable {

  /**
    * The total number of cut points across all continuous features.
    * In case of FuzzyPartitioning algorithm each cut point represents the core of each triangular fuzzy set.
    *
    * @return the total number of cut-points across all continuous features
    */
  def numFuzzySets: Int = {
    cores.map(_._2.size).sum
  }

  /**
    * The average number of cut points per continuous feature
    * In case of FuzzyPartitioning algorithm each cut point represents the core of each triangular fuzzy set.
    *
    * @return the average number of cut points per continuous feature
    */
  def averageFuzzySets: Double = {
    numFuzzySets.toDouble / cores.keySet.size
  }

  /**
    * A set of discarded continuous features. Each feature is identified by its index.
    * A feature is considered discarded when no cut points (namely fuzzy sets in case of FuzzyPartitining)
    * have been generated
    *
    * @return the set of discarded continuous features.
    */
  def discardedFeature: collection.Set[Int] = {
    cores.filter(_._2.isEmpty).keySet
  }

  /**
    * A tuple that contains the feature with the highest number of cut points, i.e. fuzzy sets in case of FuzzyPartitioning.
    *
    * @return a tuple where the first element is the index of the feature with the highest number of cut points
    *         (or fuzzy sets) and the second element is the number of cut points (or fuzzy sets) for the corresponding feature
    */
   def max: (Int, Int) = {
    cores.mapValues(_.size).maxBy(_._2)
  }

  /**
    * A tuple that contains the feature with the lowest number of cut points, i.e. fuzzy sets in case of FuzzyPartitioning.
    * Note: discarded features are not considered in the computation.
    *
    * @return a tuple where the first element is the index of the feature with the lowest number of cut points
    *         (or fuzzy sets) and the second element is the number of cut points (or fuzzy sets) for the corresponding feature
    */
  def min: (Int, Int) = {
    cores.filter(_._2.nonEmpty).mapValues(_.size).minBy(_._2)
  }

  /**
    * Java-friendly API for [[iet.unipi.bigdatamining.discretization.model.FuzzyPartitioningModel]].
    *
    * @return the cores map as instance of [[java.util.Map[java.lang.Integer, java.util.List[java.lang.Double\]\] ]]
    */
  def toJava: java.util.Map[java.lang.Integer, java.util.List[java.lang.Double]] = {
    val modelAsJava = new java.util.HashMap[Integer, java.util.List[java.lang.Double]]()
    cores.foreach(fCores =>
      modelAsJava.put(new java.lang.Integer(fCores._1),
        bufferAsJavaList(ListBuffer(fCores._2: _*)).map(x => new java.lang.Double(x))))
    modelAsJava
  }

  /**
    * The string representing the Fuzzy Partitioning model.
    */
  override def toString: String = {
    val sb = new StringBuilder(s"Number of continuous features ${cores.size} " +
      s"(${cores.size-discardedFeature.size} partitioned and ${discardedFeature.size} discarded):\n")
    cores.toSeq.sortBy(_._1).foreach(fCores => sb.append(s"\t${fCores._1} -> ${fCores._2.toString}\n"))
    sb.toString()
  }

}