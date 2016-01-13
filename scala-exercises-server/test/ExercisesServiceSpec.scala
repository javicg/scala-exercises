import models.ExerciseEvaluation
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.scalaz.DisjunctionMatchers
import services.exerciseV0.ExercisesService

@RunWith(classOf[JUnitRunner])
class ExercisesServiceSpec extends Specification with DisjunctionMatchers {

  val expectedTestLibrary = "stdlib"
  val expectedTestSection = "Extractors"
  val expectedTestExercise = "forAssigningValues"
  val expectedTestSuccesArgs = List("Chevy", "Camaro", "1978", "120")
  val expectedTestFailedArgs = List("a", "b", "1", "2")

  "ExercisesService" should {

    "return at least one library via classpath discovery" in {
      val libraries = ExercisesService.libraries
      libraries must not be empty
      libraries.find(_.name == expectedTestLibrary) must beSome
    }

    "return at least one section via classpath discovery" in {
      val foundSections = for {
        library ← ExercisesService.libraries
        sectionName ← library.sectionNames
        section ← ExercisesService.section(library.name, sectionName)
      } yield section
      foundSections must not be empty
      val expectedSection = foundSections.find(_.name == expectedTestSection)
      expectedSection must beSome
      val section = expectedSection.get
      section.exercises must not be empty
      section.exercises.find(_.method.contains(expectedTestExercise)) must beSome
    }

    "evaluate a known exercise type coercing it's parameters and get a successful result" in {
      ExercisesService.evaluate(ExerciseEvaluation(
        libraryName = expectedTestLibrary,
        sectionName = expectedTestSection,
        method = expectedTestExercise,
        args = expectedTestSuccesArgs)) must beRightDisjunction
    }

    "evaluate a known exercise type coercing it's parameters and get a failed result" in {
      ExercisesService.evaluate(ExerciseEvaluation(
        libraryName = expectedTestLibrary,
        sectionName = expectedTestSection,
        method = expectedTestExercise,
        args = expectedTestFailedArgs)) must beLeftDisjunction
    }

  }
}