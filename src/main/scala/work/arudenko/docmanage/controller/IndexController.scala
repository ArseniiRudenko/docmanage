package work.arudenko.docmanage.controller

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.{Document, LongPoint, StringField, TextField}
import org.apache.lucene.document.Field.Store
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.store.{Directory, FSDirectory, MMapDirectory}
import java.nio.file.Path
import java.time.OffsetDateTime

class IndexController(indexPath:Path){

  private val dir:Directory = FSDirectory.open(indexPath)
  private val indexWriterConfig = new IndexWriterConfig()
  private val writter = new IndexWriter(dir, indexWriterConfig)

  def getDocById(id:Long):LuceneDoc = ???

  def addDocument(doc:LuceneDoc):Unit = 
    writter.addDocument(doc.doc)
  

  def replaceDocument(id:Long,doc:LuceneDoc):Unit = ???


}




