import connection._

object Test extends Connectable {
  val begin = Signal[Unit]
  val end = Signal[Unit]
  
  def main(args: Array[String]) {
    begin()
    end()
  }
}