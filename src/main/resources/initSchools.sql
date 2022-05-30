CREATE TABLE if not exists Schools(
   School_ID  serial primary key,
   NAME       VARCHAR(50) NOT NULL UNIQUE
);
