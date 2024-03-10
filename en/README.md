# Input Validation in Java Jakarta Applications

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
* These validations should be performed on both the client and the server side. Client-side validation provides quick 
feedback to users, while server-side validation acts as the final line of defense
* Jakarta EE has a Jakarta Bean Validation API that provides a powerful framework for implementing input validation
* It offers a set of annotations and APIs (demonstrated below) that are used to define validation constraints on Java beans, DTOs, and domain objects.
```java
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

public class BeanValidationExample {
    public static class User {
        @NotNull 
        private String name;

        public User(String name) {
            this.name = name;
        }
    }

  public static void main(String[] args) {
      User user = new User(null); //invalid input
      //Get a validator instance and validate the input
      try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
          Validator validator = factory.getValidator();
          Set<ConstraintViolation<User>> violations = validator.validate(user);
          if (!violations.isEmpty()) {
              for (ConstraintViolation<User> violation : violations) {
                  System.out.println(violation.getPropertyPath() + " " + violation.getMessage());
              }
          } else {
              System.out.println("User data is valid.");
          }
      }
  }
}  
  

```

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
* Untrusted input should be normalized before validating it to avoid encoding errors or data corruption
* Input normalization is needed because
  * it could contain characters from different character sets 
  * because same character could have many representations in unicode. 
    * For e.g. character "é" can be represented in Unicode as either a single code point (U+00E9) or 
    as a combination of the letter "e" (U+0065) followed by the combining acute accent (U+0301).
* Normalizing input to a certain encoding ensures that the data has a consistent encoding across platforms and environments
* In Java, `java.text.Normalizer.normalize()` method transforms Unicode text into the standard normalization forms 
described in the Unicode Standard
* The `java.text.Normalizer.Form` enum provides different normalization forms, each represents a standardized way of encoding 
Unicode characters to ensure consistency and compatibility. The 4 supported forms are
  * NFD (Normalization Form D) - decomposes composite characters into base characters and combining characters
  * NFC (Normalization Form C) - composes characters into their precomposed forms whenever possible
  * NFKD (Normalization Form KD) - decomposes characters and also normalizes compatibility characters
  * NFKC (Normalization Form KC) - composes characters and also normalizes compatibility characters into their compatibility equivalents
* The most suitable form for performing input validation on arbitrarily encoded strings is KC (NFKC)
```java
String s = "e\u0301";
System.out.println("Is normalized : " + Normalizer.isNormalized(s, Normalizer.Form.NFKC));
s = Normalizer.normalize(s, Normalizer.Form.NFKC);
System.out.println("Is normalized : " + Normalizer.isNormalized(s, Normalizer.Form.NFKC));
```

## Syntax Validation
* Validates the syntax or structure of input data to ensure it conforms to expected patterns or formats.
* Below code sample uses jakarta bean validation annotations `@NotBlank` and `@Size` for validating
that the password is not blank and within the limits as prescribed by the application
```java
public class User {
    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
}

```

## Regular Expression Validation
* Utilizes regular expressions to define and enforce validation patterns for different types of input data.
* For e.g. below code validates email addresses through a regex
```java
  public class User {
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String emailAddress;
  }  
```

## Allow-List Validation.
* Accepts only known, expected input values or patterns while rejecting all others.
* Also referred as whitelisting, it is most of the times used to manage access and permissions within the application
* A common practice is to also have `deny-list` where specific inputs are identified as problematic and blocked
* Allow-list is more secure as it only permits known and verified entities, reducing the chances of unforeseen vulnerabilities
* For e.g. below code validates that the user role matches with the predefined roles that the application defines
```java
  String userRole = request.getParameter("role");
  switch(userRole) {
    case "admin":
    case "user":
    case "guest":
        break;
    default:
        throw new IllegalStateException("Unexpected user role : " + userRole);      
  }
```