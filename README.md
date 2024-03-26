# Flo Energy NEM12 Parser

This repo contains code for parsing meter readings from input files
in [NEM12 format](https://aemo.com.au/-/media/files/electricity/nem/retail_and_metering/market_settlement_and_transfer_solutions/2022/mdff-specification-nem12-nem13-v25.pdf?la=en)
and generating corresponding SQL insert statements.

Example NEM12 file: [sample.csv](/sample.csv)

SQL insert statements will be generated for the following table:

```sql
create table meter_readings (
  id uuid default gen_random_uuid() not null,
  "nmi" varchar(10) not null,
  "timestamp" timestamp not null,
  "consumption" numeric not null,
  constraint meter_readings_pk primary key (id),
  constraint meter_readings_unique_consumption unique ("nmi", "timestamp")
);
```

## Table of Contents

- [Plan](#plan)
    - [Stretch goals](#stretch-goals)
- [How to use](#how-to-use)
- [Testing](#testing)
- [Notes](#notes)
- [Reference document](#reference-document)

## Plan

- [x] Set up basic repo
- [x] Set up placeholder classes and write main test case
- [x] Write Nem12Parser class that can parse NEM12 files and yield meter readings
- [x] Write MeterReading class that can be used to generate SQL insert statements
- [x] Write script that takes in a NEM12 file and outputs SQL insert statements

### Stretch goals

- [x] README: Add instructions for installation, running, testing, etc...
- [ ] Set up Docker container to allow users without Java to run code & tests

## How to use

The code is written in Kotlin. Dependencies are managed with Gradle.

`ParseFile.kt` serves as an entrypoint for parsing a NEM12 file and generating the corresponding SQL insert statements.

```bash
$ gradle run --args sample.csv

# INSERT INTO "meter_readings" ("nmi","timestamp","consumption") VALUES ('NEM1201009','2005-03-01T00:30:00',0);
# INSERT INTO "meter_readings" ("nmi","timestamp","consumption") VALUES ('NEM1201009','2005-03-01T01:00:00',0);
# INSERT INTO "meter_readings" ("nmi","timestamp","consumption") VALUES ('NEM1201009','2005-03-01T01:30:00',0);
# ...
```

If you are working with Kotlin code yourself, you may prefer to directly use the Nem12Parser class that is available
in `src/main/kotlin/Nem12Parser.kt`:

```kotlin
FileInputStream("sample.csv").use { input ->
    val parser = Nem12Parser(input)
    while (parser.hasNext()) {
        val insertStmt = parser.next().toSql()
        // dsl.execute(insertStmt)
        // ...
    }
}
```

## Testing

A few unit and integration tests were written with JUnit. To run the tests, simply run the `gradle :test` command.

## Notes

- The NMI is actually not a unique identifier within the NEM12 file. One meter may have several registers and so there
  may be multiple meter readings for the same NMI and the same timestamp. See appendix H.1 in the reference document (
  page 33). It might be necessary to add another column to the database (`register`?) and expand the unique constraint
  to include this additional column as well.

## Reference document

Details about the NEM12 format are specified in this reference
document: https://aemo.com.au/-/media/files/electricity/nem/retail_and_metering/market_settlement_and_transfer_solutions/2022/mdff-specification-nem12-nem13-v25.pdf?la=en
