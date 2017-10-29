package cc.factorie.app.nlp.coref

import java.io.{BufferedInputStream, DataInputStream, DataOutputStream, FileInputStream}

import cc.factorie.model.Parameters
import cc.factorie.util.BinarySerializer
import cc.factorie.variable.{CategoricalDomain, CategoricalVectorDomain, DiscreteDomain, VectorDomain}

trait CorefModel extends Parameters {
  val MentionPairFeaturesDomain = new CategoricalVectorDomain[String] {
    dimensionDomain.maxSize = 1e6.toInt
    dimensionDomain.growPastMaxSize = false
  }
  val MentionPairCrossFeaturesDomain = new VectorDomain {
    def dimensionDomain: DiscreteDomain = new DiscreteDomain(5e6.toInt + 1)
  }

  val MentionPairLabelDomain = new CategoricalDomain[String] { this += "YES"; this += "NO"; freeze() }

  object CorefTokenFrequencies{
    var counter:TopTokenFrequencies = null
  }

  def deserialize(stream: DataInputStream) {
    val headWords = new DefaultHashMap[String,Int](0)
    BinarySerializer.deserialize(headWords, stream)
    BinarySerializer.deserialize(MentionPairFeaturesDomain, stream)
    BinarySerializer.deserialize(new CategoricalVectorDomain[String] { val domain = new CategoricalDomain[String]} , stream)
    BinarySerializer.deserialize(this, stream)
    CorefTokenFrequencies.counter = new TopTokenFrequencies(headWords)
    stream.close()
    MentionPairFeaturesDomain.freeze()
  }

  def deserialize(filename: String) {
    deserialize(new DataInputStream(new BufferedInputStream(new FileInputStream(filename))))
  }

  def serialize(stream: DataOutputStream) {
    BinarySerializer.serialize(CorefTokenFrequencies.counter.headWords,stream)
    MentionPairFeaturesDomain.freeze()
    BinarySerializer.serialize(MentionPairFeaturesDomain , stream)
    BinarySerializer.serialize(new CategoricalVectorDomain[String] { val domain = new CategoricalDomain[String]}, stream)
    BinarySerializer.serialize(this,stream)
  }

}
