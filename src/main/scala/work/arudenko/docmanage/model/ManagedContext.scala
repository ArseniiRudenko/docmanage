package work.arudenko.docmanage.model

import scalikejdbc.ConnectionPool
import work.arudenko.docmanage.controller.IndexController

import java.nio.file.Path

trait ManagedContext {
  def rootPath:Path
  def defaultLanguage:String
  def maxFileSizeForChecksum: Long
  def index:IndexController
  def db:ConnectionPool

}
