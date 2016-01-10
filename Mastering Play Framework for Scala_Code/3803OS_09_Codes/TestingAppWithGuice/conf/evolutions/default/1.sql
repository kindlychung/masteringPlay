# --- !Ups
CREATE TABLE user (
  id int(11) NOT NULL AUTO_INCREMENT,
  login_id varchar(45) NOT NULL,
  password varchar(50) NOT NULL,
  name varchar(45) NULL,
  contact_no varchar(45) NULL,
  dob date NULL,
  address varchar(45) DEFAULT NULL,
  role varchar(45) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY login_id_UNIQUE (login_id),
  UNIQUE KEY id_UNIQUE (id)
);

# --- !Downs
DROP TABLE Users;