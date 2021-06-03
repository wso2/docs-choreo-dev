# Ballerina Expression Syntax

Ballerina expressions comprise a powerful expression syntax based on the [Ballerina language](https://ballerina.io/) for function invocations, declaring a variable using literals and tuples, typecasting, etc. This document presents you with the common expressions that you can use while developing in the Choreo console.

## Variables
A variable statement holds a value of a specific data type (`string`, `integer`, etc.).  You can declare a variable and use it later in the logical process of your service or the integration.

### var

Ballerina allows you to declare variables using the `var` keyword instead of specifying a type. The expression on the right-hand side decides the type of the declared variable.

###### Examples:

To initialize a `var` in Choreo, you can use one of the following;
```ballerina
    5
    
   "Hello!"
```

### int

An integer is a positive number, a negative number, or zero that does not include a fraction. A signed integer is an integer with the  `+` or `-` character in the front, indicating the sign. You can also define a positive integer or zero using only numerical characters. 

###### Examples:

To initialize an `int` in Choreo, you can use one of the following;

  ```ballerina
  1234
  
  -10
  
  +2020
  ```

### float

A float is a number with a decimal point. You have the option to use `+` or `-` characters in the front to indicate the sign.

###### Examples:

To initialize a `float` in Choreo, you can use one of the following;
  ```ballerina
  0.3353
  
  -8593.992
  
  +950.930
  ```

### boolean

The boolean data type has one of the two possible values, `true` or `false`.

### string

A string is a sequence of characters. It is required to use double quotes (`"`)  to mark the boundary between the starting and ending characters.

###### Examples:

To initialize a `float` in Choreo, you can use one of the following;
  ```ballerina
  "Ballerina"
  
  "Ballerina is a programming language intended for network-distributed applications."
  ```

## Value/type operations

### String operations

- Concatenation

    You can use the `+` operator to do string concatenations. The string value can come from a string literal or a variable.

    ###### Example:

    To concatenate the string `Hi` and a string variable `name`, you can use the following expression:

    ```ballerina
     "Hi " + name;
    ```

### Arithmetic operators

Arithmetic operators perform addition, subtraction, multiplication, division, and modules on numerical values. You can use literals or variables of type `int` or `float` as the numerical values in Ballerina expressions.

- Addition

    You can use the `+` operator for number addition. 

    ###### Example:

    To add an integer literal (`20`) and an integer variable `number1` and store it in an integer variable `sum`, you can use the following expression:

    ```ballerina
    number1 + 20;
    ```

- Subtraction 

    You can use the `-` operator for number subtraction. 

    ###### Example:

    To subtract two integers, integer variable `yearOfBirth` from integer literal `2022` and print the result, you can use the following expression:

    ```ballerina
    2022 - yearOfBirth;
    ```
 
- Division

    You can use the `/` operator for number division. 

    ###### Example:

    To divide an integer variable `totalNumberOfStudents` from integer literal `5` and store it in an integer variable `numberOfStudentsPerClass`, you can use the following expression:

    ```ballerina
      totalNumberOfStudents / 5; 
    ```
 
- Multiplication

    You can use the `*` operator for number multiplication. 

    ###### Example:

    To multiply two float constants `60` by `5` and store it in a float variable `numberOfMinsIn5Hours`, you can use the following expression:

    ```ballerina
      60 * 5;
    ```

- Modulo 

    You can use the  `%` operator to get the remainder of a division. 

    ###### Example:

    To get the remainder of the division between an integer variable `n` and an integer literal `3`, you can use the following expression:
  
    ```ballerina
    (n % 3) > 2
    ```

### Value/Type Operators

- Equality 

    You can use the equality operators to check the equality of values of any of the [Ballerina language's](https://ballerina.io/) basic types (`string`, `int`, `float`, `decimal`, `boolean`, and `nil`). These values can come from a literal or a variable. **Note** that the resulting value from these operators is always of `boolean` type.   
    
    Ballerina provides the following equality operators: 
    
    - `==`: For deep value equality of anydata|error typed values.
    - `===`: For reference equality. 
    - `!= `and `!==`: For negations.

    ###### Examples:

    To compare an integer literal `10` with an integer variable `firstNumber`, you can use the following expression:
    
    ```ballerina
    10 == firstNumber
    ```

    To compare a string variable `username`  with  a string literal `admin`, use the following expression:  

    ```ballerina
    username != "admin" 
    ```

- Numeric value comparison

    The following operators are available in the expression editor for numeric value comparisons:
    
    - `<`: Less than
    - `>`: Greater than
    - `<=`: Less than or equal to
    - `>=`: Greater than or equal to

    ###### Examples:

    To compare the value of the integer variable `firstNumber` to the integer literal `10`, use the following expressions:

    ```ballerina
    firstNumber > 10
  
    firstNumber <= 10
    ```

## HTTP 

All the HTTP-related service applications created in Choreo have a variable called `request` (of type `http:Request`) which the developers can use to query information related to the HTTP request. Following is a list of such use cases.

### Reading a header value in the request
A typical HTTP request contains many headers.  These include standard and custom headers. You can use the `request` variable's `getHeader` function to read a specific header value in any expression-enabled input field.

###### Examples:

To read the value of an HTTP header `Access-Control-Allow-Origin`, use the following expression:

  ```ballerina
  check request.getHeader("Access-Control-Allow-Origin")
  
  ```
  
To read the value of an HTTP header by passing a variable that contains the header name `headerName`, use the following expression:

  ```ballerina
  check request.getHeader(headerName)
  ```

### Checking whether a header is present in the request

Applications sometimes need to check the availability of a header before proceeding to something like reading the header value. You can use the  `hasHeader`  function in the `request` object for this purpose.

###### Examples:

To check if the HTTP header `content-length` exists given the header name, use the following expression:

  ```ballerina
  request.hasHeader("content-length")
  ```
  
To check if the HTTP header `content-length` exists by passing the header name in the string variable `contentLength`, use the following expression: 

  ```ballerina
  request.hasHeader(contentLength)
  ```
### Reading the JSON Payload of the request

JSON is a common content type used for HTTP communication. You can use the `request` variable’s `getJsonPayload` function to read the JSON payload sent with the HTTP request. Note that the `getJsonPayload` function’s return type is `json|ClientError`.

###### Example:

To read the JSON Payload of the HTTP request, use the following expression:

  ```ballerina
  request.getJsonPayload()
  ```

### Reading the Text Payload of the request

Sometimes, content is sent as a string (plain text) with the HTTP request. You can use the `request` variable’s `getTextPayload` function to read the text payload sent with the HTTP request. Note that the `getTextPayload` function’s return type is `string|ClientError`.

###### Example:

To read the text payload of the HTTP request, use the following expression:

  ```ballerina
  check request.getTextPayload()
  ```

### Reading the value of a query parameter 

Query parameters are used in an HTTP request to send additional inputs to consider when processing the request. You can use the `request` variable's `getQueryParamValue` function to read the passed query parameter.

###### Examples:

To read the value of the query parameter `category`, use the following expression:

  ```ballerina
  request.getQueryParamValue("category")
  ```
  
To read the value of the query parameter passing the query parameter name as a variable, use the following expression:

  ```ballerina
  request.getQueryParamValue(queryParamName)
  ```

### Reading the cookies available in the request

HTTP cookies keep stateful information against a client. You can use the `request` variable's `getCookies` function to read cookies present in the request as an array.

###### Example:

To read all the cookies available in the request, use the following expression:

  ```ballerina
  request.getCookies()
  ```

## JSON

JSON is a textual format for representing a collection of values: a simple value, an array of values, or an object. Ballerina has a single type named `json` that can represent any JSON value. Thus, `json` is a built-in union type in Ballerina that can contain values of the types `nil` (as the null value), `boolean`,` int`,` float`, `decimal`, `string`, `json[]`, and `map<json>`.

### Simple values in JSON 

Ballerina has a single type named `json` that can represent any JSON value. You can use `json` which is a built-in union type in Ballerina, to store values of the types `nil` (as the null value), `boolean`, `int`, `float`, `decimal`, `string`, `json[]`, and `map<json>`. 

###### Examples:

To initialize a `json` type variable, you can use the following:
```ballerina
    "Alex"

    5.36
    
    false
    
    [1, false, null, "foo", {first: "Alex", last: "John"}]
    
    {name: "Alex", age: null, marks: {math: 90, language: 95}}
    
     null
```

### JSON Objects

Ballerina represents JSON objects as maps. You can define a JSON object to hold values of any `json` type (`string`, `number`, `boolean`, `object`, `array`, or `null`). A JSON object can be declared in two ways:
    -  Using the variable type `json`.
    -  Using the variable type `other` and `map<json>` as the `other type`.

###### Examples:


To declare a JSON object, use the following:

```ballerina
     {name: "apple", color: "red", price: "20.0"}    
```

To create an empty JSON object, use the following:

```ballerina
    {}
```

To cast a `json` type variable `fruitDetail` to a `map<json>` type variable, use the following:

```ballerina

<map<json>>fruitDetail
 
```

To access or change a field of the JSON object `fruitDetailObject`, use one of the following:

!!! note
    If you define the JSON object using a `json` type variable, you need to cast it to a `map<json>` first.
    
```ballerina
   fruitDetailObject["color"] = "orange"; 
   
   fruitDetailObject["name"];
```

To create a nested JSON object, use the following:

```ballerina
    map<json> family = {
           fname: "Alex",
           lname: "Stallone",
           address: {
               line: "20 Palm Grove",
               city: "Colombo 03",
               country: "Sri Lanka"
           }
       };
```

### JSON Arrays

JSON array values must be of any of the json types: `string`, `number`, `boolean`, `object`, `array`, or `null`. You can define a JSON array in one of the following two ways:
    - Using a [`json` type variable](#simple-values-in-JSON).  
    - Using the variable type `other` and `json[]` as the `other type`.
JSON array elements can be accessed by index.

###### Examples:

To declare a JSON array, use the following:

```ballerina
 [1, false, null, "foo", {first: "Alex", last: "John"}]
 
```
To cast the `json` type variable `personRecord` to a `json[]` type variable, use the following:

```ballerina
<json[]>personRecord;
```

To access elements the 4th element in the JSON array `personRecordArray`, use one of the following:

!!! note
    If you define the JSON array using a `json` type variable, you need to cast it to a `json[]` type first.
    
```ballerina
personRecordArray[4]; 
```

To add a JSON array in an object literal, use the following:

```ballerina
    {
        fname: "Alex",
        lname: "Stallone",
        family: [
            {fname: "James", lname: "Stallone"},
            {fname: "Blake", lname: "Stallone"},
            {fname: "Kyle", lname: "Stallone"},
            {fname: "Taylor", lname: "Stallone"}
        ]
    }
```

To get the length of a JSON array, use the following:

```ballerina
personRecordArray.length(); 
```

### JSON access

You can use the field access(.) and optional field access (?.) to access the `json` type in Ballerina. In this case, the type checking happens at runtime. The return type of this operation is a union of `json` and `error`. 
    
!!! info
    The examples in this section will assume the following json is already defined.
    
    ```ballerina
         json person = {
             fname: "Alex",
             lname: "Jane",
             address: {
                 line: "20 Palm Grove",
                 city: "Colombo 03",
                 country: "Sri Lanka"
             }
         }
    ```    

- Field access(.) 

    You can use the field access operator to access record fields of the `json` type. If the key is invalid or the field access occurs on `null`, it returns `error`.
   
    ###### Examples:

    To access a field, use the following:    

    ```ballerina
       check person.fname;
    ```
    To access a field in a nested level, use the following:
 
     ```ballerina
        check person.address.city;
     ```

- Optional field access (?.) 

    You can use the optional field access operator to access record fields, including any optional fields. If the key is invalid or the field access occurs on `null`, it returns `null ` or `()`.

    ```ballerina
    check person?.address?.city;
    ```

### JSON/Record/Map Conversion

Ballerina records, maps, and JSON objects hold records. Records are collections of fields, and you can access each field value by a key. You can write expressions converting from one type to another.

###### Examples:

To convert the record `movieTheRevenant` to `json`, use the following:

```ballerina
      check movieTheRevenant.cloneWithType(json);
      
```

To convert the json `theRevenantJson` to record `Movie`, use the following:

```ballerina
     check movieTheRevenant.cloneWithType(movie);
```

To convert the record `movieTheRevenant` to a  map type `MapAnydata map<anydata>`, use the following:

```ballerina
     check movieTheRevenant.cloneWithType(MapAnydata);
```

