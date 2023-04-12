
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source
import scala.util.Try
import scala.util.{Success, Failure}


class StudentMarks {

  //calculate grades by taking the path as input parameter
  def calculateGrades(path: String): Future[Double] = {
    val parsedCsv = parseCsv(path)
    val studentAverages = calculateStudentAverages(parsedCsv)
    val classAverage = calculateClassAverage(studentAverages)
    classAverage.recover {
      case e: Exception => throw new Exception("Failed to calculate grades: " + e.getMessage)
    }
  }

  //parse the csv file to return this Future[List[Map[String,String]]]
  def parseCsv(path: String): Future[List[Map[String, String]]] = {
    val file = Try(Source.fromFile(path))
    file match {
      case Success(f) =>
        val lines = f.getLines.toList
        f.close()
        val header = lines.head.split(",").map(_.trim)
        val data = lines.tail.map(_.split(",").map(_.trim)).map(row => header.zip(row).toMap)
        Future.successful(data)
      case Failure(ex) =>
        Future.failed(ex)
    }
  }

  // calculateStudent Average and grades and returns the Future[List[String,Double,String]]
  def calculateStudentAverages(data: Future[List[Map[String, String]]]): Future[List[(String, Double, String)]] = {
    data.map { dataList =>
      dataList.map { dataMap =>
        val studentId = dataMap("StudentID")
        val grades = List(dataMap("English"), dataMap("Physics"), dataMap("Chemistry"), dataMap("Maths")).map(_.toDouble)
        val avgGrade = grades.sum / grades.size
        if (avgGrade >= 90)
          (studentId, avgGrade,"A")
        else if (avgGrade < 90 && avgGrade >= 80)
          (studentId, avgGrade,"B")
        else if (avgGrade < 80 && avgGrade >= 70)
          (studentId, avgGrade,"C")
        else if (avgGrade < 70 && avgGrade >= 60)
          (studentId, avgGrade,"D")
        else if (avgGrade < 60 && avgGrade >= 50)
          (studentId, avgGrade,"E")
        else (studentId, avgGrade,"Fail")
      }
    }
  }

  //calculate the ClassAverages and return  Future[Double]
  def calculateClassAverage(studentAverages: Future[List[(String, Double, String)]]): Future[Double] = {
    studentAverages.map { studentAveragesList =>
      val classAverage = studentAveragesList.map(_._2).sum / studentAveragesList.size
      classAverage
    }
  }
}

