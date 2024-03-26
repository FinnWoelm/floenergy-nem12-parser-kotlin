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
                "INSERT INTO \"meter_readings\" (\"nmi\",\"timestamp\",\"consumption\") VALUES ('test','2024-03-20T15:05:00',3.51);",
                reading.toSql()
            )
        }
    }
}