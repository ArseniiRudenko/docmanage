package work.arudenko.docmanage.model

import com.google.common.hash.Hashing
import com.typesafe.scalalogging.{LazyLogging, Logger}
import org.apache.lucene.document.Field.Store
import org.apache.lucene.document.{Document, LongPoint, StringField}
import org.apache.tika.Tika
import org.apache.tika.metadata.Metadata
import org.apache.tika.mime.MediaType
import org.apache.tika.parser.{AutoDetectParser, ParseContext, Parser}
import org.apache.tika.sax.BodyContentHandler
import org.joda.time.DateTimeZone
import org.xml.sax.ContentHandler
import scalikejdbc._
import work.arudenko.docmanage.controller._
import java.util.UUID
import java.io.{FileInputStream, FileNotFoundException, InputStream, Reader}
import java.nio.file.Path
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.security.MessageDigest
import java.time.{Instant, OffsetDateTime}
import java.util
import scala.util.{Failure, Try, Using}
import com.google.common.io.{Files => GuavaFiles}
case class FileMetadata(
                         id:Long,
                         filePath:String,
                         isDirectory:Boolean,
                         fileSize:Option[Long],
                         fileChecksum:Option[String],
                         fileCreatedAt:OffsetDateTime,
                         fileIUpdatedAt:OffsetDateTime,
                         createdAt:OffsetDateTime,
                         updatedAt:OffsetDateTime) {

  def updateMetadata():Unit = ???

}



object FileMetadata extends LazyLogging{


  private val tika = new Tika
  given Conversion[Instant,OffsetDateTime] with
    override def apply(x: Instant): OffsetDateTime =
      OffsetDateTime.ofInstant(x,DateTimeZone.getDefault.toTimeZone.toZoneId)


  def create(path:Path)(using m:ManagedContext):Try[FileMetadata] = {
    //Parser method parameters
    val file = path.toFile
    if(!file.exists())
      return Failure(new FileNotFoundException(s"there is no file at path ${path.toString}"))

    val attr = Files.readAttributes(path, classOf[BasicFileAttributes])
    val size:Option[Long] = if attr.isRegularFile then Some(attr.size()) else None
    val created:OffsetDateTime = attr.creationTime().toInstant
    val modified:OffsetDateTime = attr.lastModifiedTime().toInstant
    val hash: Option[String] =
      if (attr.isRegularFile)
        Try(GuavaFiles.asByteSource(file).hash(Hashing.sha256()).toString).toOption
      else
        None
    //create lucene doc
    val doc = LuceneDoc()
    doc.addTimestamp("created", created)
    doc.addTimestamp("modified", modified)
    size.foreach(s=>doc.addLong("size",s))
    hash.foreach(h=>doc.addString("sha256hash", h))

    //create db record
    Using(m.db.borrow()) { conn =>
      val db: DB = DB(conn)
      val id: Long = db localTx { implicit session =>
        sql"""
                  insert into FILE_METADATA (file_path, file_size, file_checksum, file_created_ts, file_modified_ts,IS_FOLDER)
                  values (${path.toString},$size,$hash,$created,$modified,${attr.isDirectory})
          """.updateAndReturnGeneratedKey.apply()
      }
      doc.addId(id)

      if (attr.isRegularFile) {
        //parse doc content
        Using(new FileInputStream(file)) { is =>
          val metadata = new Metadata()
          val reader = tika.parse(is, metadata)
          //index content
          doc.addText("content", reader)
          //index metadata
          metadata
            .names()
            .map(name => (name, metadata.get(name)))
            .foreach(pair => doc.addText(pair._1, pair._2))
          m.index.addDocument(doc)
        }.recover(
          error =>{
            logger.error("failed tika parsing, content is not indexed", error)
            m.index.addDocument(doc)
          }
        )
      }else{
        m.index.addDocument(doc)
      }

      //return record
      FileMetadata(
        id,
        path.toString,
        attr.isDirectory,
        size,
        hash,
        created,
        modified,
        Instant.now(),
        Instant.now()
      )
    }




  }

}
