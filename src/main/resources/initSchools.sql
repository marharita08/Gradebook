CREATE TABLE if not exists Schools(
   School_ID  serial primary key,
   NAME       VARCHAR(100) NOT NULL UNIQUE,
   photo      VARCHAR(100)
);
