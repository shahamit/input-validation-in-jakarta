# Input Validation in Java Jakarta

# What is Input Validation

* In Java Jakarta applications, user input comes from either REST APIs, JSP (JavaServerPages) 
or JMS (Java Message Service) 
  * In a REST API, it could be accepted through 
    * http headers
    * query or path parameters 
    * request body. 
  * In JMS, it could be part of the messages sent to the message queue. 
  * In JSP pages, input may be submitted through HTML forms or request parameters
* Input validation is a process of inspecting and validating this data to ensure it meets certain criteria.
* A standard criteria is to validate the syntax, semantics, length and the format of the input data. 

# Benefits of Input Validation as a security measure

* Mitigate the risk of injection attacks like SQL injection  
  * For e.g. by inserting `' OR '1'='1` on the login form, an attacker can bypass authentication checks
* Prevent buffer overflows and data truncation 
  * For e.g. reading a long input like `thisisaverylongusername` from `request.getParameter("username");` without any length 
  validation could lead to data truncation potentially causing data loss.
* Enhanced data integrity 
  * For e.g. user age accepted without numeric value validation could allow non-numeric characters or negative values  
* Improve user experience and usability with informative error messages and feedback when input validations fail.

# Types of input validation
## Normalizing Input to UTF-8 
* Input containing characters from different character sets like ISO-8859-1, may lead to encoding errors or 
data corruption
* Normalizing input to a certain encoding ensures that the data has a consistent encoding across platforms and environments
* In Java, `java.nio.charset.StandardCharsets` and `String` class methods are used for input normalization. Sample code 
is as below
```java
  String inputData = "Café";
  byte[] utf8Bytes = inputData.getBytes(StandardCharsets.UTF_8);
  String normalizedInput = new String(utf8Bytes, StandardCharsets.UTF_8);
```

## Syntax Validation
* Validates the syntax or structure of input data to ensure it conforms to expected patterns or formats. 
* For e.g. checking if a user's age is within the syntactical rules defined for valid ages (between 18 and 100 years)

## Regular Expression Validation
* Utilizes regular expressions to define and enforce validation patterns for different types of input data.
* For e.g. below code validates email addresses throw regex
```java
  String userEmail = "example@example.com";
  userEmail.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");  
```

## Allow-List Validation.
* Accepts only known, expected input values or patterns while rejecting all others.
* For e.g. below code validates that the user role matches with the predefined roles that the application understands
```java
  String[] allowList = {"admin", "user", "guest"};
  String userRole = request.getParameter("role");
  for (String allowed : allowList) {
    if (userRole.equals(allowed)) 
        return true;
  }
  throw new IllegalStateException("Unexpected user role : " + userRole);
```