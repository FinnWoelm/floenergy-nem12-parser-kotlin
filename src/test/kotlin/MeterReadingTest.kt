import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class MeterReadingTest {
    @DisplayName("to_sql")
    @Nested
    inner class ToSql {
        @Test
        fun testItReturnsSqlInsertStatement() {
            val reading = MeterReading(
                nmi = "test",
                timestamp = LocalDateTime.of(2024, 3, 20, 15, 5),
                consumption = BigDecimal("3.51")
            )
            assertEquals(
                "insert into meter_readings (nmi, timestamp, consumption) values ('test', '2024-03-20 15:05:00.0', 3.51);",
                reading.toSql()
            )
        }

        @Test
        fun testItProducesValidSqlInsertStatement() {
            // Create mock table
            // Note: This does not match the actual target table. But that's fine, since we do not want/need to test the
            // unique constraints and UUID generation (etc.) here in this test.
            val dsl = DSL.using("jdbc:sqlite::memory:")
            dsl.createTable("meter_readings")
                .column("nmi", SQLDataType.VARCHAR(10).notNull())
                .column("timestamp", SQLDataType.LOCALDATETIME.notNull())
                .column("consumption", SQLDataType.NUMERIC.notNull())
                .execute()

            // Insert our record
            val reading = MeterReading(
                nmi = "test",
                timestamp = LocalDateTime.of(2024, 3, 20, 15, 5),
                consumption = BigDecimal("3.51"),
            )
            dsl.execute(reading.toSql())

            // Verify that our record exists
            val row = dsl.fetchSingle("SELECT * FROM meter_readings LIMIT 1")

            assertEquals(row.get("nmi"), "test")
            assertEquals(row.get("timestamp", String::class.java), "2024-03-20 15:05:00.0")
            assertEquals(row.get("consumption"), BigDecimal("3.51"))
        }
    }
}