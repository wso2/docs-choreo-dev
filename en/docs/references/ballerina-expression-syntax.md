# Ballerina Expression Syntax

Ballerina expressions comprise a powerful expression syntax, which is based on the [Ballerina language](https://ballerina.io/) for function invocations, declaring a variable using literals and tuples, typecasting, etc. This document presents the common expressions that you can use while developing Choreo applications.

## Supported literals 

### String

A string is simply a sequence of characters. Double quotes (`"`) are required to mark the boundary between the starting and ending characters.

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

### Signed integer

A signed integer is an integer with a  `+` or `-` character preceding the number, indicating a positive or negative number. You can also define a positive integer or zero using only numeric characters without the sign. Using the signed integer type, you can provide positive integers, negative integers, and zeros as input to an expression-supported field.

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

### Decimal floating-point number

A decimal floating-point number is a number with a decimal point. You can precede the numbers with a `+` or `-` character to indicate the sign.

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
  0
  ```

### Boolean 

The boolean data type has one of two possible values: `true` or `false`.

#### Examples

  ```ballerina
  true
  ```
  ```ballerina
  false
  ```

## Accessing variables

### Using the variable value

Variables keep values in memory for later access. In an expression syntax-supported input field, you can refer to these variables directly using the variable name.

#### Example

- This converts the value of an integer variable (`total`) to a string:

  ```ballerina
  total.toString()
  ```

## Supported operators

### String concatenation

You can use the `+` operator for string concatenations. The string value can come from a string literal or a variable.

#### Examples

- Concatenates two string literals:
 
  ```ballerina
  "Hello " + "world!" 
  ```
  
- Concatenates a string literal and a variable (`name`):
 
  ```ballerina
  "Hi " + name
  ```
  
- Concatenates two string variables (`firstName` and `lastName`):
 
  ```ballerina
  firstName + lastName
  ```

### Numeric addition
You can use the `+` operator for numeric addition. The number values can come from a literal or from a variable.

#### Examples

- Adds integer literals:

  ```ballerina
  10 + 20
  ```
  
- Adds decimal literals:

  ```ballerina
  0.56 + 1.84
  ```
  
- Adds a literal and a numerical variable (`firstNumber`):

  ```ballerina
  firstNumber + 100
  ```
  
- Adds two numerical variables (`firstNumber` and `secondNumber`):

  ```ballerina
  firstNumber + secondNumber
  ```

### Numeric subtraction

You can use the `-` operator for numeric subtraction. The number values can come from a literal or from a variable.

#### Examples

- Subtracts integer literals:

  ```ballerina
  20 - 10
  ```
  
- Subtracts decimal literals:

  ```ballerina
  1.84 - 0.56  
  ```
  
- Subtracts a literal from a numerical variable (`firstNumber`):

  ```ballerina
  firstNumber - 100
  ```
  
- Subtracts a numeric variable (`secondNumber`) from another numeric variable (`firstNumber`):

  ```ballerina
  firstNumber - secondNumber
  ```

### Numeric division

You can use the `/` operator for numeric division. The number values can come from a literal or from a variable.

#### Examples

- Divides integer literals:

  ```ballerina
  20 / 10 
  ```
  
- Divides decimal literals:

  ```ballerina
  1.84 / 0.56  
  ```
  
- Divides a numeric variable (`firstNumber`) by a literal:

  ```ballerina
  firstNumber / 100
  ```
  
- Divides a numeric variable (`firstNumber`) by another numeric variable (`secondNumber`):

  ```ballerina
  firstNumber / secondNumber
  ```

### Numeric multiplication

You can use the `*` (asterisk) operator for numeric multiplication. The number values can come from a literal or from a variable.

#### Examples

- Multiplies integer literals:

  ```ballerina
  10 * 20
  ```
  
- Multiplies decimal literals:

  ```ballerina
  0.56 * 1.84
  ```
  
- Multiplies a numeric variable (`firstNumber`) by a literal:

  ```ballerina
  firstNumber * 100
  ```
  
- Multiplies a numeric variable (`firstNumber`) by another numeric variable (`secondNumber`):

  ```ballerina
  firstNumber * secondNumber
  ```

### Modulo operation

You can use the `%` operator to get the remainder of a division. The number values can come from a literal or from a variable.

#### Examples

- Calculates the modulus of two integer literals:

  ```ballerina
  10 % 4
  ```
  
- Calculates the modulus of a literal and a variable (`firstNumber`):

  ```ballerina
  firstNumber % 100 
  ```
  
- Calculates the modulus of two numeric variables (`firstNumber` and `secondNumber`):

  ```ballerina
  firstNumber % secondNumber
  ```

### Checking the equality of basic types 

In the [Ballerina language](https://ballerina.io/), basic types are `string`, `int`, `float`, `decimal`, `boolean`, and `nil`. You can compare the values of these types using the `==` binary operator. The values can come from a literal or from a variable. Similarly, the `!=` operator is used to check the inequality. Note that the resulting value from these operators is always of `boolean` type. 

#### Examples

- Checks if two integer literals are equal:

  ```ballerina
  10 == 4
  ```
  
- Checks if two string literals are equal:

  ```ballerina
  "hi" == "hi"
  ```
  
- Checks if the literal and the value of a variable (`firstNumber`) is equal:

  ```ballerina
   firstNumber == 100
  ```
  
- Checks if the values of two variables (`firstNumber` and `secondNumber`) are equal. The following returns true when the value of the two variables is equal:

  ```ballerina
  firstNumber == secondNumber
  ```
  
- Checks if the values of two variables (`firstNumber` and `secondNumber`) are **not** equal. The following returns true when the value of the two variables are different:

  ```ballerina
  firstNumber != secondNumber
  ```

### Checking the type of variable

The `is` operator asserts the type of a variable.

#### Example

- Checks if a variable (`payload`) is of JSON type:

  ```ballerina
  payload is json
  ```

### Numeric value comparisons

The following operators are available in the expression editor for numeric value comparisons:

- `<` (less than)
- `>` (greater than)
- `<=` (less than or equal to)
- `>=` (greater than or equal to)

#### Examples

- Checks if the value of a variable (`firstNumber`) is greater than a literal:

  ```ballerina
  firstNumber > 10
  ```
  
- Checks if the value of a variable (`firstNumber`) is less than or equal to the value of another variable (`secondNumber`):

  ```ballerina
  firstNumber <= secondNumber
  ```

## HTTP request related operations

All HTTP-related service applications created in Choreo have a variable called `request` (of type `http:Request`), which you can use to query information related to an HTTP request. Following is a list of such common use cases:

### Reading a header value in the request
A typical HTTP request contains many headers.  These include standard and custom headers. You can use the `getHeader` function of the `request` variable to read a specific header value in any expression-enabled input field.

#### Examples

- Reads the value of an HTTP header on passing the header name (`Access-Control-Allow-Origin`):

  ```ballerina
  check request.getHeader("Access-Control-Allow-Origin")
  
  ```
  
- Reads the value of an HTTP header on passing a variable that contains the header name (`headerName`). Note the omission of the double quotes:

  ```ballerina
  check request.getHeader(headerName)
  ```

### Checking whether a header is present in the request

Applications sometimes need to check the availability of a header before proceeding to something like reading the header value. You can use the `hasHeader`  function of the `request` object for this purpose.

#### Examples

- Checks if a particular HTTP header (`content-length`) exists, given the header name:

  ```ballerina
  request.hasHeader("content-length")
  ```
  
- Checks if a particular HTTP header (`content-length`) exists by passing a variable (`contentLength`) containing the header name. Note the omission of the double quotes:

  ```ballerina
  request.hasHeader(contentLength)
  ```

### Reading the JSON payload of the request

JSON is a common content type used for HTTP communication. You can use the `getJsonPayload` function of the `request` variable to read the JSON payload sent with the HTTP request. Note that the return type of the `getJsonPayload` function is `json|ClientError`.

#### Example 

- Reads the JSON payload of the HTTP request:

  ```ballerina
  request.getJsonPayload()
  ```

### Reading the text payload of the request

Sometimes content is sent as a string (plain text) with the HTTP request. You can use the `getTextPayload` function of the `request` variable to read the text payload sent with the HTTP request. Note that the return type of the `getTextPayload` function is `string|ClientError`.

#### Example

- Reads the text payload of the HTTP request:

  ```ballerina
  check request.getTextPayload()
  ```

### Reading the query parameter value

Query parameters are used in an HTTP request to send additional inputs to consider when processing the request. You can use the `getQueryParamValue` function of the `request` variable to read query parameter values that are passed.

#### Examples

- Reads the query parameter value given the query parameter name (`category`):

  ```ballerina
  request.getQueryParamValue("category")
  ```
  
- Reads the query parameter value by passing a variable that contains the query parameter name. Note the omission of the double quotes:

  ```ballerina
  request.getQueryParamValue(queryParamName)
  ```

### Reading the cookies available in the request

HTTP cookies keep stateful information about a client. You can use the `getCookies` function of the `request` variable to read cookies present in the request as an array.

#### Example

- Reads all the cookies available in the request:

  ```ballerina
  request.getCookies()
  ```


