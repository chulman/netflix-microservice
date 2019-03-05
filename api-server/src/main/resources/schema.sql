DROP TABLE IF EXISTS NOTIFICATION;


CREATE TABLE NOTIFICATION (
  ID BIGINT NOT NULL AUTO_INCREMENT,
  APNS_ID varchar(36) NOT NULL,
  DEVICE_TOKEN VARCHAR(64) NOT NULL,
  OS VARCHAR(10) NOT NULL,
  BUNDLE varchar(100) NOT NULL,
  PAYLOAD varchar(4000) NOT NULL,
  STATUS varchar(10),
  createdAt TIMESTAMP NOT NULL default now(),
  PRIMARY KEY (ID)
);
