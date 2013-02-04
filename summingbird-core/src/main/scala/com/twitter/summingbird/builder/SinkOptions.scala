package com.twitter.summingbird.builder

import com.twitter.summingbird.storm.StormMetric

/**
 *  @author Oscar Boykin
 *  @author Sam Ritchie
 */

sealed trait SinkOption extends Serializable

// SinkParallelism controls the number of executors storm allocates to
// the groupAndSum bolts. Each of these bolt executors is responsible
// for storing and committing some subset of the keyspace to the
// Sink's store, so higher parallelism will result in higher load on
// the store. The default sink parallelism is 5.

case class SinkParallelism(parHint: Int) extends SinkOption

// MaxWaitingFutures is the maximum number of key-value pairs that the
// SinkBolt in Storm will process before starting to force the
// futures. For example, setting MaxWaitingFutures(100) means that if
// a key-value pair is added to the OnlineStore and the (n - 100)th
// write has not completed, Storm will block before moving on to the
// next key-value pair.

case class MaxWaitingFutures(get: Int) extends SinkOption

// RpcParallelism controls the number of processes Storm allocates to
// the Rpc Return bolts. The default rpc parallelism is 10.

case class RpcParallelism(parHint: Int) extends SinkOption

// DecoderParallelism controls the number of processes Storm allocates
// to the Decoder bolts. The decoder bolts recieve DRPC requests,
// decode them into a Key instance and pass this key along to the sink
// for lookup and return (via the ReturnResults bolt). The default
// decoder parallelism is 10.

case class DecoderParallelism(parHint: Int) extends SinkOption

case class OnlineSuccessHandler(handlerFn: Unit => Unit) extends SinkOption

case class OnlineExceptionHandler(handlerFn: PartialFunction[Throwable, Unit])
    extends SinkOption

// See FlatMapOptions.scala for an explanation.
object SinkStormMetrics {
  def apply(metrics: => TraversableOnce[StormMetric[_]]) = new SinkStormMetrics(() => metrics)
  def unapply(metrics: SinkStormMetrics) = Some(metrics.metrics)
}
class SinkStormMetrics(val metrics: () => TraversableOnce[StormMetric[_]]) extends SinkOption

// True if the Monoid is commutative, false otherwise.

case class MonoidIsCommutative(isCommutative: Boolean) extends SinkOption
