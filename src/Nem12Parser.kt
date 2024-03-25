import java.io.InputStream
import java.math.BigDecimal
import java.time.LocalDateTime

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

    /**
     * Returns the next MeterReading in the input stream
     */
    override operator fun next(): MeterReading {
        return MeterReading("TODO", LocalDateTime.now(), BigDecimal("0.0"))
    }

    /**
     * Returns `true` if input stream has more meter readings
     */
    override operator fun hasNext(): Boolean {
        return false
    }
}