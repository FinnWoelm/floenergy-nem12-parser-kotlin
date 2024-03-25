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
- [Notes](#notes)
- [Reference document](#reference-document)

## Plan

- [x] Set up basic repo
- [x] Set up placeholder classes and write main test case
- [x] Write Nem12Parser class that can parse NEM12 files and yield meter readings
- [ ] Write MeterReading class that can be used to generate SQL insert statements
- [ ] Write script that takes in a NEM12 file and outputs SQL insert statements

### Stretch goals

- [ ] README: Add instructions for installation, running, testing, etc...
- [ ] Set up Docker container to allow users without Java to run code & tests

## Notes

- The NMI is actually not a unique identifier within the NEM12 file. One meter may have several registers and so there
  may be multiple meter readings for the same NMI and the same timestamp. See appendix H.1 in the reference document (
  page 33). It might be necessary to add another column to the database (`register`?) and expand the unique constraint
  to include this additional column as well.

## Reference document

Details about the NEM12 format are specified in this reference
document: https://aemo.com.au/-/media/files/electricity/nem/retail_and_metering/market_settlement_and_transfer_solutions/2022/mdff-specification-nem12-nem13-v25.pdf?la=en
