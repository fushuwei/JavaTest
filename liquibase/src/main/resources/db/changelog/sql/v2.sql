-- liquibase formatted sql

-- changeset liquibase:v2
CREATE TABLE test_table3 (test_id int(11), test_column varchar(255));

-- changeset liquibase:v3
alter table test_table2 modify column test_column varchar(100);
