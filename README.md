# Revolut Backend Developer Task.

Design and implement a RESTful API (including data model and the backing implementation)
for money transfers between accounts.

## How To Build:

mvn clean install

## How to Run the application :

mvn exec:java

or 

java -jar /target/revolut-money-transfer-task-0.0.1-SNAPSHOT-jar-with-dependencies.jar

## How to Run test cases : 

mvn test


## Implementation details : 

1. Jersey implementation of JAX-RS is used to develop the RESTful Web service.
2. Application uses H2 as in-memory datastore.
3. Embedded jetty server is used to run the APIs.
4. Dependency injection is done using HK2 provided by JAX-RS.
5. API ensures Concurrency in any scenario. It is achieved by using the ACID property of DB. Optimistic locking is achieved when the DB has the control to update the rows. Updates are handled by DB it makes sure that the amount is calculated and updated based on the current account balance so in cases of concurrent requests the updates would always check for the existing balance of the source account.
6. Integration test and unit tests provide an overall code coverage of 88.7. (Usage of Powermockito reduces some coverage).


## API Details:

1. POST /banking/createAccount  -- Creates a new bank account.

Sample Request:

{
	"accountId": "11234",
	"balance": 1000
}


2. GET /banking/accountDetails/{accountNumber} -- Fetches account details by accountNumber.


3. POST /banking/transact -- Transfers money from one account to another.

Request Structure:

{
"sourceAccountId" : "12346",
"targetAccountId":"56789",
"amount" : 1000
}

##

### Http Response Codes :
All the responses contain specific messages along with the status codes.

1. 200 -- Http Ok.
2. 400 -- Bad Request.
3. 404 -- Not Found.
4. 500 -- Internal Server Error. (Incase of any unforseen errors)
