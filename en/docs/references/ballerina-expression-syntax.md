# Ballerina Expression Syntax

Ballerina expressions comprise a powerful expression syntax based on the [Ballerina language](https://ballerina.io/) for function invocations, declaring a variable using literals and tuples, typecasting, etc. This document presents you the common expressions that you can use while developing Choreo applications.

## Supported Literals 

### String

A string is simply a sequence of characters. It is required to use double quotes (`"`)  to mark the boundary between the starting and ending characters.

#### Examples

  ```ballerina
  "Colombo"
  ```
  ```ballerina
  "12345"
  ```
  ```ballerina
  "Ballerina is a programming language intended for network-distributed applications."
  ```

### Signed Integer

Using this type, you can give numbers as an input to an expression-supported field.  You have the option to use `+` or `-` characters in the front to indicate the sign.  You can also define an integer by using only numerical characters without any delimiters. 

#### Examples

  ```ballerina
  1234
  ```
  ```ballerina
  -10
  ```
  ```ballerina
  +2020
  ```

### Decimal Floating-Point Number

A number with a decimal point falls under this data type. You have the option to use `+` or `-` characters in the front to indicate the sign.

#### Examples

  ```ballerina
  0.3353
  ```
  ```ballerina
  2044.24
  ```
  ```ballerina
  -8593.992
  ```
  ```ballerina
  +950.930
  ```
  ```ballerina
  10.0
  ```

### Boolean Value

This data type has one of two possible values, `true` and `false`.

#### Examples

  ```ballerina
  true
  ```
  ```ballerina
  false
  ```

## Accessing Variables

### Using the variable value

Variables keep values in memory for later access. In an expression syntax-supported input field, you can refer to these variables directly using the variable name.

#### Example

  ```ballerina
  VariableName
  ```

## Supported Operators

### String Concatenation

You can use the `+` operator to do string concatenations. The string value can come from a string literal or a variable.

#### Examples

 - Concatenating two string literals.
 
  ```ballerina
  "Hello " + "world!" 
  ```
 - Concatenating a string literal and a variable.
 
  ```ballerina
  "Hi " + nameVar
  ```
 - Concatenating two string variables.
 
  ```ballerina
  firstNameString + lastNameString
  ```

### Number Addition
You can use the  `+` operator for number addition. The number values can come from a literal or a variable.

#### Examples
- Concatenating two integer literals.

  ```ballerina
  10 + 20
  ```
- Concatenating two decimal literals.

  ```ballerina
  0.56 + 1.84
  ```
- Concatenating a literal and a variable.

  ```ballerina
  100 + nameVariable
  ```
- Concatenating two variables.

  ```ballerina
  firstNumber + secondNumber
  ```

### Number Subtraction

You can use the `-` operator for number subtraction. The number values can come from a literal or a variable.

#### Examples
- Subtracting two integer literals.

  ```ballerina
  10 - 20
  ```
- Subtracting two decimal literals.

  ```ballerina
  0.56 - 1.84
  ```
- Subtracting a literal and a variable.

  ```ballerina
  100 - <named variable>
  ```
- Subtracting two variables.

  ```ballerina
  <first number> - <second number>
  ```

### Number Division

You can use the `/` operator for number division. The number values can come from a literal or a variable.

#### Examples

- Division between two integer literals.

  ```ballerina
  10 / 20
  ```
- Division between two decimal literals.

  ```ballerina
  0.56 / 1.84
  ```
- Division between a literal and a variable.

  ```ballerina
  100 / nameVariable
  ```
- Division between two variables.

  ```ballerina
  firstNumber - secondNumber
  ```

### Number Multiplication

You can use the `*` operator for number multiplication. The number values can come from a literal or a variable.

#### Examples

- Multiplying two integer literals.

  ```ballerina
  10 * 20
  ```
- Multiplying two decimal literals.

  ```ballerina
  0.56 * 1.84
  ```
- Multiplying a literal and a variable.

  ```ballerina
  100 * nameVariable
  ```
- Multiplying two variables.

  ```ballerina
  firstNumber * secondNumber
  ```

### Modulo Operation

You can use the  `%` operator to get the remainder of a division. The number values can come from a literal or a variable.

#### Examples
- Modulus of two integer literals.

  ```ballerina
  10 % 4
  ```
- Modulus of a literal and a variable.

  ```ballerina
  100 % nameVariable
  ```
- Modulus of two variables.

  ```ballerina
  firstNumber % secondNumber
  ```

### Checking The Equality of Basic Types 

In the [Ballerina language](https://ballerina.io/), basic types are `string`, `int`, `float`, `decimal`, and `boolean`. You can compare the values of these types using the `==` binary operator. The values can come from a literal or a variable. Similarly `!=` operator is used to check the inequality. Note that the resulting value from these operators is always of `boolean` type. 

#### Examples
- Equality of two integer literals.

  ```ballerina
  10 == 4
  ```
- Equality of two string literals.

  ```ballerina
  "hi" == "hi"
  ```
- Equality of a literal and a variable.

  ```ballerina
  100 == expectedValueVariable
  ```
- Equality of two variables.

  ```ballerina
  firstNumber == secondNumber
  ```
- Inequality of two variables. The following returns true when the values of the two variables are different.

  ```ballerina
  firstNumber != secondNumber
  ```

### Checking the Type of Variable

The `is` operator is used to assert the type of a variable.

#### Examples
- Checking whether the variable is of JSON type.

  ```ballerina
  payload is JSON
  ```

### Numerical Value Comparisons

The following operators are available in the expression editor for numeric value comparisons:
- `<` (less than)
- `>` (greater than)
- `<=` (less than or equal to)
- `>=` (greater than or equal to)

#### Examples
- Checking if the value of a variable is greater than 10.

  ```ballerina
  myVar > 10
  ```
  
- Comparing two variables.

  ```ballerina
  myProperty <= limit
  ```

## HTTP request related operations

All the HTTP-related service applications created in Choreo have a variable called `req` (of type `http:Request`) which the developers can use to query information related to the HTTP request. Following is a list of use cases that an integration application developer would need more likely need to develop Choreo applications

### Reading a header value in the request
A typical HTTP request contains many headers. Some of these headers are standard, and some are custom. In an expression-enabled input field, you can use the `req` variable's `getHeader` function to read a specific header value.

#### Examples
- Reading the value of Access-Control-Allow-Origin HTTP header.

  ```ballerina
  req.getHeader("Access-Control-Allow-Origin")
  
  ```
- Using a value stored in a variable as the header name. Please note the omission of the double quotes.

  ```ballerina
  req.getHeader(headerName)
  ```

### Checking whether a header is present in the request

Applications sometimes need to check the availability of a header before proceeding to something like reading the header value. You can use the  `hasHeader`  function in the `req` object for this purpose.

#### Examples
- Checking whether the content-length property is available as a header.

  ```ballerina
  req.hasHeader("content-length")
  ```
- Using a value stored in a variable as the query parameter name. Please note the omission of the double quotes.

  ```ballerina
  req.hasHeader(myCustomeHeader)
  ```

### Reading the JSON Payload of the request

JSON is a common content type used for HTTP communication. The `req` variable’s `getJsonPayload` function can be used to read the JSON payload sent with the HTTP request. Note that the `getJsonPayload` function’s return type is `json|ClientError`.

#### Examples
- Reading the JSON Payload of the HTTP request.

  ```ballerina
  req.getJsonPayload()
  ```

### Reading the Text Payload of the request

Sometimes, content is sent as a string (plain text) with the HTTP request. The `req` variable’s `getTextPayload` function can be used to read the text payload sent with the HTTP request. Note that the `getTextPayload` function’s return type is `string|ClientError`.

#### Examples
- Reading the Text Payload of the HTTP request.

  ```ballerina
  req.getTextPayload()
  ```

### Reading the query parameter value

Query parameters are used in an HTTP request to send additional inputs to consider when processing the request. You can use the `req` variable's `getQueryParamValue` function to read the passed query parameter.

#### Examples
- Reading the query parameter named category.

  ```ballerina
  req.getQueryParamValue("category")
  ```
- Using a value stored in a variable as the query parameter name. Note the omission of the double quotes.

  ```ballerina
  req.getQueryParamValue(queryParamName)
  ```

### Reading the cookies available in the request

HTTP Cookies keep stateful information against a client. You can use the `req` variable's `getCookies` function to read cookies present in the request as an array.

#### Examples
- Reading all the cookies available in the request.

  ```ballerina
  req.getCookies()
  ```