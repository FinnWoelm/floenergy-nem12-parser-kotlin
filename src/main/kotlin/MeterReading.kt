import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * A single reading from a meter.
 *
 * @property nmi National meter(ing) identifier
 * @property timestamp Timestamp of the reading
 * @property consumption Total amount of energy (or other measured value)
 */
data class MeterReading(val nmi: String, val timestamp: LocalDateTime, val consumption: BigDecimal) {

    /**
     * Generates an SQL insert statement for the meter reading. The statement can be used to insert the data into an
     * SQL database with `meter_readings` table.
     *
     * @return SQL insert statement
     */
    fun toSql(): String {
        return "TODO"
    }
}