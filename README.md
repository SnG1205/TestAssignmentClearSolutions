# Project`s guide

### Test assignment for ClearSolutions

In order to fulfill all the requirements project has the following structure:
* data package with objects` classes that are used in the controller
* exceptions package with custom exceptions and a handler for REST error handling
* util package with json converter class that turns any object into a JSON format response
* Controller class which handles all the incoming requests
* Spring application class

UserControllerTest class in test folder contains unit tests for UserController class. 
After running all the tests line coverage percentage equals 100%.

### Important note
Since user data is not being stored in database, lists are used.
Delete, put, patch requests use an id to find a specific user, even though user`s id is not constant and changes everytime preceding users in a list are deleted.