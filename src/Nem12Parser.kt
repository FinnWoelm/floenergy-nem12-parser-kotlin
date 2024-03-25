import java.io.BufferedInputStream
import java.io.InputStream
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Scanner
import kotlin.NoSuchElementException

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

    // The next meter reading to yield from the iterator
    private var next: MeterReading? = null

    // Several variables to store state as we parse through the stream
    private var currentNmi: String? = null
    private var currentInterval: Int? = null
    private var currentDate: LocalDate? = null

    // Measurements counts indicates the expected number of measurements in the current interval data record
    private var measurementsCount: Int = 0

    // Measurements index keeps track of the number of the current measurement in the current interval data record
    private var measurementsIndex: Int = 0

    // Number of measurements left
    private val measurementsLeft get() = this.measurementsCount - this.measurementsIndex

    // Record indicator codes from the NEM12 definitions
    private val NMI_DETAILS_RECORD_CODE = 200
    private val INTERVAL_DATA_RECORD_CODE = 300

    // Format of dates according to the NEM12 definition
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    // Number of minutes in a day
    private val MINUTES_PER_DAY = 60 * 24

    /**
     * Returns the next MeterReading in the input stream
     *
     * The iterator design follows these principles: https://stackoverflow.com/a/29061917
     * It supports both forms of iteration:
     * - while (it.hasNext()) it.next()
     * - try { while (true) it.next(); } catch ...
     *
     * @return the next meter reading
     * @throws NoSuchElementException when iterator is empty
     */
    override operator fun next(): MeterReading {
        // If we have a next reading, return it and reset next to null
        if (hasNext()) {
            val reading = next!!;
            next = null;
            return reading;
        };

        // If we have no next reading, throw error
        throw NoSuchElementException();
    }

    /**
     * Indicates whether the input stream has meter readings left
     *
     * @return `true` if the stream contains more meter readings
     */
    override operator fun hasNext(): Boolean {
        // If we don't yet know the next value, let's get it
        if (next == null) next = getNextOrNull()

        // Return true if next value was found
        return next != null
    }

    /**
     * Gets the next meter reading from the input stream, if any.
     *
     * @return next meter reading (or null, if none left)
     */
    private fun getNextOrNull(): MeterReading? {
        // If there are no measurements left in the current line, scan through the lines until we reach the next
        // interval data record
        if (measurementsLeft == 0) {
            try {
                advanceToNextIntervalDataRecord()
            }

            // If no next interval data record was found, a NoSuchElementException is thrown
            catch (e: NoSuchElementException) {
                scanner.close()
                return null
            }
        }

        // Get consumption from next token
        val consumption = BigDecimal(scanner.next())

        // First reading is for the period ending at date plus interval, for example: 00h05 or 00h30
        // Last reading is for the period ending at midnight of next day.
        val timestamp = currentDate!!.atStartOfDay().plusMinutes((currentInterval!! * (measurementsIndex + 1)).toLong())

        // Initialise reading
        val reading = MeterReading(nmi = currentNmi!!, timestamp = timestamp, consumption = consumption);

        // Advance measurement index for keeping track of current measurement number
        measurementsIndex++;

        // When measurements in the current line are exhausted, skip to the next line
        if (measurementsLeft == 0) scanner.nextLine();

        return reading;
    }

    /**
     * Advances the scanner to the next interval data record and sets state variables (NMI, interval, etc...). The next
     * token from the scanner after this function has completed, will be a measurement.
     *
     * @throws NoSuchElementException if end of stream is reached without finding interval data record
     */
    private fun advanceToNextIntervalDataRecord() {
        while (true) {
            // Get the record indicator
            val recordIndicator = scanner.nextInt()

            when (recordIndicator) {
                // The details record provides the NMI and interval info
                NMI_DETAILS_RECORD_CODE -> {
                    currentNmi = scanner.next()
                    for (i in 3..8) scanner.next() // Skip from 3rd the 8th element (inclusive)
                    currentInterval = scanner.nextInt()
                    scanner.nextLine()
                }

                // The interval data record provides date and measurements
                INTERVAL_DATA_RECORD_CODE -> {
                    currentDate = LocalDate.parse(scanner.next(), DATE_FORMATTER)
                    measurementsIndex = 0
                    measurementsCount = MINUTES_PER_DAY / currentInterval!!

                    // The next token will be a measurement, so we can end the function here
                    return;
                }

                // Ignore any other codes
                else -> scanner.nextLine()
            }
        }
    }
}