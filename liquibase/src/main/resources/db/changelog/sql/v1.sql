-- liquibase formatted sql

-- changeset liquibase:v1
CREATE TABLE test_table1
(
    test_id     int(11),
    test_column varchar(255)
);

CREATE TABLE test_table2
(
    test_id     int(11),
    test_column varchar(255)
);

-- changeset liquibase:v4
alter table test_table2 modify column test_column varchar(200);
