package scp

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

case class Vertex(vid: Int) extends Ordered[Vertex]{

  private var in_edges: RDD[Edge] = _
  private var out_edges: RDD[Edge] = _

  def get_in_edges(): RDD[Edge] = {
    if (in_edges == null) throw new ExceptionInInitializerError("Incoming edges not set for this vertex")
    else in_edges
  }
  def get_out_edges(): RDD[Edge] = {
    if (out_edges == null) throw new ExceptionInInitializerError("Outgoing edges not set for this vertex")
    else out_edges
  }
  def set_in_edges(inEdges: Seq[Edge]): Unit = {
    inEdges.map(edge => {
      if (edge.dst.vid != vid) {
        throw new IllegalArgumentException("Setting a wrong incoming edge")
        null
      }
    })
    in_edges = SparkContext.getOrCreate().parallelize(inEdges)
  }
  def set_out_edges(outEdges: Seq[Edge]): Unit = {
    outEdges.map(edge => {
      if (edge.src.vid != vid) {
        throw new IllegalArgumentException("Setting a wrong outgoing edge")
        null
      }
    })
    out_edges = SparkContext.getOrCreate().parallelize(outEdges)
  }

  def indgr: Long = in_edges.count()
  def outdgr: Long = out_edges.count()

  def in_vertices: RDD[Vertex] = in_edges.map(x => x.src)
  def out_vertices: RDD[Vertex] = out_edges.map(x => x.dst)

  override def compare(that: Vertex): Int = this.vid - that.vid
//  override def compare(that: Vertex): Int = (outdgr - that.outdgr).toInt

  def canEqual(a: Any): Boolean = a.isInstanceOf[Vertex]
  override def equals(that: Any): Boolean =
    that match {
      case that: Vertex => that.canEqual(this) && this.vid == that.vid
      case _ => false
    }
}
