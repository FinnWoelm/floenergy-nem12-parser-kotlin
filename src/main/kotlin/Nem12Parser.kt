import java.io.BufferedInputStream
import java.io.InputStream
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Scanner
import kotlin.NoSuchElementException

// Record indicator codes for NEM12 files
object Codes {
    const val NMI_DETAILS_RECORD = 200
    const val INTERVAL_DATA_RECORD = 300
    const val END_OF_DATA_RECORD = 900
}

/**
 * Parse a NEM12 input stream and yield MeterReadings.
 *
 * Iteratively parses the NMI data details record (200) and interval data
 * records (300) in the input stream and yields instances of MeterReadings.
 * Each MeterReading represents the total amount of energy measured at the
 * given meter and timestamp.
 *
 * Data is parsed iteratively, so even large NEM12 files can be handled well.
 *
 * @property input Input stream to parse
 */
class Nem12Parser(private val input: InputStream) : Iterator<MeterReading> {
    // Scan through the input stream in a buffered way
    // This allows us to read large files
    private val scanner = Scanner(BufferedInputStream(input)).useDelimiter("[,\n]")

    // Iterator for meter readings
    private val iterator = parseAsSequence().iterator()

    // Format of dates according to the NEM12 definition
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd")

    // Number of minutes in a day
    private val MINUTES_PER_DAY = 60 * 24

    /**
     * Returns the next MeterReading in the input stream
     *
     * @return the next meter reading
     * @throws NoSuchElementException when iterator is empty
     */
    override operator fun next(): MeterReading {
        return iterator.next()
    }

    /**
     * Indicates whether the input stream has meter readings left
     *
     * @return `true` if the stream contains more meter readings
     */
    override operator fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    private fun parseAsSequence() = sequence {
        var currentNmi: String? = null
        // Current interval is long because the plusMinutes() function below requires a long
        var currentInterval: Long? = null

        while (scanner.hasNext()) {
            // Get the record indicator
            val recordIndicator = scanner.nextInt()

            when (recordIndicator) {
                // The details record provides the NMI and interval info
                Codes.NMI_DETAILS_RECORD -> {
                    currentNmi = scanner.next()
                    for (i in 3..8) scanner.next() // Skip from 3rd the 8th element (inclusive)
                    currentInterval = scanner.nextLong()
                }

                // The interval data record provides date and measurements
                Codes.INTERVAL_DATA_RECORD -> {
                    val currentDate = LocalDate.parse(scanner.next(), DATE_FORMATTER)
                    val numReadings = MINUTES_PER_DAY / currentInterval!!

                    // Yield individual measurements
                    for (i in 1..numReadings) {
                        val consumption = BigDecimal(scanner.next())
                        val timestamp = currentDate.atStartOfDay().plusMinutes((currentInterval * i))
                        yield(MeterReading(nmi = currentNmi!!, timestamp = timestamp, consumption = consumption))
                    }
                }

                // The end of data record indicates end of file
                Codes.END_OF_DATA_RECORD -> break
            }

            // Skip the remainder of the current line
            scanner.nextLine()
        }

        scanner.close()
    }
}