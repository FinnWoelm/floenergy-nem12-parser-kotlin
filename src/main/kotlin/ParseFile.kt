import java.io.FileInputStream
import kotlin.system.exitProcess

/**
 * Takes the file name of a NEM12 file as input and returns SQL insert statements for the contained meter readings. The
 * file name should be passed as the first and only argument.
 *
 * Example: gradle run --args sample.csv
 */
fun main(args: Array<String>) {
    if (args.count() != 1) {
        println("Please provide exactly one filename argument")
        exitProcess(1)
    }

    val fileName = args[0];

    // Automatically close file input stream when we are done
    FileInputStream(fileName).use { input ->

        // Initialize the parser and print each SQL insert statement
        val parser = Nem12Parser(input)
        while (parser.hasNext()) {
            println(parser.next().toSql())
        }
    }
}