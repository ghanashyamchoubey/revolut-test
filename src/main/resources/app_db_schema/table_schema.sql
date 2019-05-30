CREATE TABLE IF NOT EXISTS Account (
  accountId VARCHAR(250) NOT NULL,
  balance DECIMAL(19,2) NOT NULL,
  PRIMARY KEY (accountId)
);

CREATE TABLE IF NOT EXISTS Transaction (
  transactionId VARCHAR(250) NOT NULL,
  sourceAccountId VARCHAR(250) NOT NULL,
  targetAccountId VARCHAR(250) NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
PRIMARY KEY (transactionId)
)