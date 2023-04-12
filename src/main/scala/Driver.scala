
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object Driver extends App {

  private val csvFilePath ="src/Resources/StudentDetails.csv"
  private val studentMarks = new StudentMarks()

  val parsedCsv: Future[List[Map[String, String]]] = studentMarks.parseCsv(csvFilePath)
  val studentAverages: Future[List[(String, Double, String)]] = studentMarks.calculateStudentAverages(parsedCsv)
  val classAverage: Future[Double] = studentMarks.calculateClassAverage(studentAverages)
  private val classGrades: Future[Double] = studentMarks.calculateGrades(csvFilePath)

  classAverage.onComplete {
    case Success(value) => println(s"The class average is $value")
    case Failure(exception) => println(s"Failed to calculate class average: ${exception.getMessage}")
  }

  studentAverages.onComplete {
    case Success(value) => println(s"The Students Average and Grade is  ${value.mkString}")
    case Failure(exception) => println(s"Failed to calculate student averages: ${exception.getMessage}")
  }

  classGrades.onComplete {
    case Success(value) => println(s"The class grade $value")
    case Failure(exception) => println(s"Failed to calculate class grades: ${exception.getMessage}")
  }

  Await.ready(classGrades, 10.seconds)

}
