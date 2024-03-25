import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.FileInputStream
import java.math.BigDecimal
import java.time.LocalDateTime

class Nem12ParserTest {
    @DisplayName("Parsing sample NEM12 file (sample.csv)")
    @Nested
    inner class ParsingSampleFile {

        @Test
        fun testItYieldsCorrectNumberOfMeterReadings() {
            val parser = Nem12Parser(FileInputStream("sample.csv"))
            assertEquals(8 * 48, parser.asSequence().toList().count())
        }

        @Test
        fun testItYieldsCorrectValues() {
            val parser = Nem12Parser(FileInputStream("sample.csv"))
            // Get the readings from the first `300` record
            val readings = parser.asSequence().take(48).toList()

            // The first reading is for the period ending at midnight + interval period (00h30 in this case).
            // Refer to page 7 in reference document.
            assertEquals(
                MeterReading(
                    nmi = "NEM1201009",
                    timestamp = LocalDateTime.of(2005, 3, 1, 0, 30),
                    consumption = BigDecimal(0),
                ), readings[0]
            )
            assertEquals(
                MeterReading(
                    nmi = "NEM1201009",
                    timestamp = LocalDateTime.of(2005, 3, 1, 6, 30),
                    consumption = BigDecimal("0.461"),
                ), readings[12]
            )
            assertEquals(
                MeterReading(
                    nmi = "NEM1201009",
                    timestamp = LocalDateTime.of(2005, 3, 1, 15, 0),
                    consumption = BigDecimal("0.555"),
                ), readings[29]
            )

            // The last reading is for the period ending at midnight (00h00 on the next day).
            // Refer to page 7 in reference document.
            assertEquals(
                MeterReading(
                    nmi = "NEM1201009",
                    timestamp = LocalDateTime.of(2005, 3, 2, 0, 0),
                    consumption = BigDecimal("0.231")
                ), readings[47]
            )
        }

        @Test
        fun testItYieldsCorrectValuesForLast300Record() {
            val parser = Nem12Parser(FileInputStream("sample.csv"))
            // Get the readings from the last `300` record
            val readings = parser.asSequence().toList().takeLast(48)

            // The first reading is for the period ending at midnight + interval period (00h30 in this case).
            // Refer to page 7 in reference document.
            assertEquals(
                MeterReading(
                    nmi = "NEM1201009",
                    timestamp = LocalDateTime.of(2005, 3, 4, 0, 30),
                    consumption = BigDecimal(0),
                ), readings[0]
            )

            assertEquals(
                MeterReading(
                    nmi = "NEM1201009",
                    timestamp = LocalDateTime.of(2005, 3, 4, 7, 0),
                    consumption = BigDecimal("0.415"),
                ), readings[13]
            )

            // The last reading is for the period ending at midnight (00h00 on the next day).
            // Refer to page 7 in reference document.
            assertEquals(
                MeterReading(
                    nmi = "NEM1201009",
                    timestamp = LocalDateTime.of(2005, 3, 5, 0, 0),
                    consumption = BigDecimal("0.355"),
                ), readings[47]
            )
        }
    }
}