DROP database if exists simulation;
create database simulation;
DROP USER IF EXISTS 'appuser'@'localhost';
CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'password';
GRANT SELECT, INSERT, UPDATE, DELETE ON simulation.* TO 'appuser'@'localhost';

USE simulation;

create table service_point(
  id int not null AUTO_INCREMENT,
  arrived int not null,
  serviced int not null,
  active_time int not null,
  total_time int not null,
  PRIMARY KEY(id)
);

create table customer(
  id int not null AUTO_INCREMENT,
  response_time int not null,
  PRIMARY KEY(id)
);

-- NOTE: the following is an example of how to use the tables
-- Also note the order of operations, especially with the foreign key

INSERT INTO service_point (
  arrived, serviced, active_time,total_time
) VALUES ( 123, 123, 123123, 123321 );

select * from service_point;

INSERT INTO customer (
  response_time) VALUES ( 123123 );

select * from customer;
DELETE FROM customer;
DELETE FROM service_point;

