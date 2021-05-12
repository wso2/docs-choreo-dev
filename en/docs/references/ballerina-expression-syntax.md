# Ballerina Expression Syntax

Ballerina expressions comprise a powerful expression syntax based on the [Ballerina language](https://ballerina.io/) for function invocations, declaring a variable using literals and tuples, typecasting, etc. This document presents you with the common expressions that you can use while developing Choreo applications.

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

A signed integer is an integer with the  `+` or `-` character in the front, indicating the sign. You can also define a positive integer or zero using only numerical characters without the sign. Using the signed integer type, you can give positive integers, negative integers, and zero as an input to an expression-supported field.

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

A decimal floating-point number is a number with a decimal point. You have the option to use `+` or `-` characters in the front to indicate the sign.

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

The boolean data type has one of the two possible values, `true` or `false`.

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

- Converting the value of the integer variable (`total`) to a string value.
  ```ballerina
  total.toString()
  ```

## Supported Operators

### String Concatenation

You can use the `+` operator to do string concatenations. The string value can come from a string literal or a variable.

#### Examples

- Concatenating two string literals.
 
  ```ballerina
  "Hello " + "world!" 
  ```
  
- Concatenating a string literal and a variable (`name`).
 
  ```ballerina
  "Hi " + name
  ```
  
- Concatenating two string variables (`firstName` and `lastName`).
 
  ```ballerina
  firstName + lastName
  ```

### Number Addition
You can use the `+` operator for number addition. The number values can come from a literal or a variable.

#### Examples

- Adding integer literals.

  ```ballerina
  10 + 20
  ```
  
- Adding decimal literals.

  ```ballerina
  0.56 + 1.84
  ```
  
- Adding a literal and a numerical variable (`firstNumber`).

  ```ballerina
  firstNumber + 100
  ```
  
- Adding two numerical variables (`firstNumber` and `secondNumber`).

  ```ballerina
  firstNumber + secondNumber
  ```

### Number Subtraction

You can use the `-` operator for number subtraction. The number values can come from a literal or a variable.

#### Examples

- Subtracting integer literals .

  ```ballerina
  20 - 10
  ```
  
- Subtracting decimal literals.

  ```ballerina
  1.84 - 0.56  
  ```
  
- Subtracting a literal from a numerical variable (`firstNumber`).

  ```ballerina
  firstNumber - 100
  ```
  
- Subtracting a numerical variable (`secondNumber`) from another numerical variable (`firstNumber`).

  ```ballerina
  firstNumber - secondNumber
  ```

### Number Division

You can use the `/` operator for number division. The number values can come from a literal or a variable.

#### Examples

- Dividing integer literals.

  ```ballerina
  20 / 10 
  ```
  
- Dividing decimal literals.

  ```ballerina
  1.84 / 0.56  
  ```
  
- Dividing a numerical variable (`firstNumber`) by a literal.

  ```ballerina
  firstNumber / 100
  ```
  
- Dividing a numerical variable (`firstNumber`) by another numerical variable (`secondNumber`).

  ```ballerina
  firstNumber / secondNumber
  ```

### Number Multiplication

You can use the `*` operator for number multiplication. The number values can come from a literal or a variable.

#### Examples

- Multiplying integer literals.

  ```ballerina
  10 * 20
  ```
  
- Multiplying decimal literals.

  ```ballerina
  0.56 * 1.84
  ```
  
- Multiplying a numerical variable (`firstNumber`) by a literal.

  ```ballerina
  firstNumber * 100
  ```
  
- Multiplying a numerical variable (`firstNumber`) by another numerical variable (`secondNumber`).

  ```ballerina
  firstNumber * secondNumber
  ```

### Modulo Operation

You can use the  `%` operator to get the remainder of a division. The number values can come from a literal or a variable.

#### Examples

- Calculating the modulus of two integer literals.

  ```ballerina
  10 % 4
  ```
  
- Calculating the modulus of a literal and a variable (`firstNumber`).

  ```ballerina
  firstNumber % 100 
  ```
  
- Calculating the modulus of two numerical variables (`firstNumber` and `secondNumber`).

  ```ballerina
  firstNumber % secondNumber
  ```

### Checking The Equality of Basic Types 

In the [Ballerina language](https://ballerina.io/), basic types are `string`, `int`, `float`, `decimal`, `boolean`, and `nil`. You can compare the values of these types using the `==` binary operator. The values can come from a literal or a variable. Similarly `!=` operator is used to check the inequality. Note that the resulting value from these operators is always of `boolean` type. 

#### Examples

- Checking if two integer literals are equal.

  ```ballerina
  10 == 4
  ```
  
- Checking if two string literals are equal.

  ```ballerina
  "hi" == "hi"
  ```
  
- Checking if the literal and the value of a variable (`firstNumber`) is equal.

  ```ballerina
   firstNumber == 100
  ```
  
- Checking if the values of two variables (`firstNumber` and `secondNumber`) are equal. The following returns true when the value of the two variables is equal.

  ```ballerina
  firstNumber == secondNumber
  ```
  
- Checking if the values of two variables (`firstNumber` and `secondNumber`) are **not** equal. The following returns true when the value of the two variables are different.

  ```ballerina
  firstNumber != secondNumber
  ```

### Checking the Type of Variable

The `is` operator is used to assert the type of a variable.

#### Example

- Checking if a variable (`payload`) is of JSON type.

  ```ballerina
  payload is json
  ```

### Numerical Value Comparisons

The following operators are available in the expression editor for numeric value comparisons:

- `<` (less than)
- `>` (greater than)
- `<=` (less than or equal to)
- `>=` (greater than or equal to)

#### Examples

- Checking if the value of a variable (`firstNumber`) is greater than a literal.

  ```ballerina
  firstNumber > 10
  ```
  
- Checking if the value of a variable (`firstNumber`) is less than or equal to the value of another variable (`secondNumber`).

  ```ballerina
  firstNumber <= secondNumber
  ```

## HTTP request related operations

All the HTTP-related service applications created in Choreo have a variable called `request` (of type `http:Request`) which the developers can use to query information related to the HTTP request. Following is a list of such common use cases.

### Reading a header value in the request
A typical HTTP request contains many headers.  These include standard and custom headers. You can use the `request` variable's `getHeader` function to read a specific header value in any expression-enabled input field.

#### Examples

- Reading the value of an HTTP header (`Access-Control-Allow-Origin`) passing the header name.

  ```ballerina
  check request.getHeader("Access-Control-Allow-Origin")
  
  ```
  
- Reading the value of an HTTP header by passing a variable that contains the header name (`headerName`). Note the omission of the double-quotes.

  ```ballerina
  check request.getHeader(headerName)
  ```

### Checking whether a header is present in the request

Applications sometimes need to check the availability of a header before proceeding to something like reading the header value. You can use the  `hasHeader`  function in the `request` object for this purpose.

#### Examples

- Checking if a particular HTTP header (`content-length`) exists given the header name.

  ```ballerina
  request.hasHeader("content-length")
  ```
  
- Checking if a particular HTTP header (`content-length`) exists by passing a variable (`contentLength`) containing the header name. Note the omission of the double-quotes.

  ```ballerina
  request.hasHeader(contentLength)
  ```

### Reading the JSON Payload of the request

JSON is a common content type used for HTTP communication. The `request` variable’s `getJsonPayload` function can be used to read the JSON payload sent with the HTTP request. Note that the `getJsonPayload` function’s return type is `json|ClientError`.

#### Examples

- Reading the JSON Payload of the HTTP request.

  ```ballerina
  request.getJsonPayload()
  ```

### Reading the Text Payload of the request

Sometimes, content is sent as a string (plain text) with the HTTP request. The `request` variable’s `getTextPayload` function can be used to read the text payload sent with the HTTP request. Note that the `getTextPayload` function’s return type is `string|ClientError`.

#### Examples

- Reading the Text Payload of the HTTP request.

  ```ballerina
  check request.getTextPayload()
  ```

### Reading the query parameter value

Query parameters are used in an HTTP request to send additional inputs to consider when processing the request. You can use the `request` variable's `getQueryParamValue` function to read the passed query parameter.

#### Examples

- Reading the query parameter value given the query parameter name (`category`).

  ```ballerina
  request.getQueryParamValue("category")
  ```
  
- Reading the query parameter value by passing a variable that contains the query parameter name. Note the omission of the double-quotes.

  ```ballerina
  request.getQueryParamValue(queryParamName)
  ```

### Reading the cookies available in the request

HTTP cookies keep stateful information against a client. You can use the `request` variable's `getCookies` function to read cookies present in the request as an array.

#### Examples

- Reading all the cookies available in the request.

  ```ballerina
  request.getCookies()
  ```



