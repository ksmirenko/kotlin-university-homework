package patterns.singleton
import java.util.ArrayList
import java.util.Random

var instance : Application? = null

class Application {
   companion object Create {
      fun getInstance(): Application? {
         if (instance == null) {
            instance = Application();
         }
         return instance
      }
   }

   fun loadWinSize(dimension : String) : Int {
      print("Computing window with $dimension")
      return Random().nextInt(100)
   }

   var listOfWindows : Array<ArrayList<Int>> = arrayOf(arrayListOf<Int> (), arrayListOf<Int> ())
   fun saveWinSize(dimension : String, size : Int) {
      if (dimension == "width") {
         println("Saving window width")
         listOfWindows[0].add(listOfWindows[0].size, size)
      }
      if (dimension == "height") {
         println("Saving window height")
         listOfWindows[1].add(listOfWindows[1].size, size)
      }
   }
}

class Window (
        private var width : Int,
        private var height : Int
) {
   fun createAppWindow (app : Application?) {
      when (app) {
         null -> {
            width = 0
            height = 0
         }
         else -> {
            width = app.loadWinSize("width")
            println ("width...")
            height = app.loadWinSize("height")
            println ("height...")
            app.saveWinSize("width", width)
            app.saveWinSize("height", height)
         }
      }
   }
}

fun main (args : Array<String>){
   var app1 = Application.getInstance()
   var firstWindow = Window(0,0)
   firstWindow.createAppWindow(app1)
   var app2 = Application.getInstance()
   var secondWindow = Window(0,0)
   secondWindow.createAppWindow(app2)
   print ("Number of application windows: ")
   println(app2?.listOfWindows?.get(0)?.size ?: 0)
}
