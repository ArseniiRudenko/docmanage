package work.arudenko.docmanage.controller
import org.apache.lucene.document.Field.Store
import org.apache.lucene.document.{Document, LongPoint, NumericDocValuesField, StoredField, StringField, TextField}

import java.io.Reader
import java.time.OffsetDateTime

case class LuceneDoc (doc: Document = new Document){
    def addTimestamp(field: String, o: OffsetDateTime): Unit = {
      doc.add(new StringField(field, o.toString, Store.YES))
      doc.add(new NumericDocValuesField(s"${field}_rq", o.toEpochSecond))
    }

    def addLong(field: String,value:Long): Unit = {
      doc.add(new StoredField(field,value))
      doc.add(new NumericDocValuesField(s"${field}_rq",value))
    }

    def addString(field: String,value:String):Unit = {
      doc.add(new StringField(field, value, Store.YES))
    }

    def addId(id:Long):Unit = {
      doc.add(new LongPoint("id",id))
    }

    def addText(field: String, value: String): Unit = {
      doc.add(new TextField(field, value, Store.YES))
    }
    def addText(field: String, text: Reader): Unit = {
      doc.add(new TextField(field, text))
    }

}
