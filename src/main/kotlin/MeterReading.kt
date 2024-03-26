import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.ParamType
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.table
import java.math.BigDecimal
import java.time.LocalDateTime


val dsl: DSLContext = DSL.using(SQLDialect.SQLITE)

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
        val query = dsl.insertInto(
            table("meter_readings"), field("nmi"), field("timestamp"), field("consumption")
        ).values(nmi, timestamp, consumption)

        return query.getSQL(ParamType.INLINED) + ";"
    }
}