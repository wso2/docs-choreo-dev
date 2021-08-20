# Ballerina Expression Syntax

Ballerina expressions comprise a powerful expression syntax based on the [Ballerina language](https://ballerina.io/) for method invocations, declaring a variable, etc. This topic presents the common expressions you can use while developing in the Choreo Console.

## Variables
A variable declaration statement declares a value of a specific type (`string`, `integer`, etc.).  You can use a previously declared variable at a later phase in the logical process of your service or integration.

### int

The `int` type is a 64-bit two's complement integer (similar to `long` in Java). A signed integer is an integer with the  `+` or `-` character preceding the integer. However, you can also define a positive integer or zero using only numeric characters. The `int` type supports the usual [arithmetic operators:](#arithmetic-operators) `+`, `-`, `/`, `*`,` %`. The operator precedence is the same as C. Note that integer overflow is a runtime error in Ballerina.

###### Examples

To initialize a variable of type `int` in Choreo, you can use one of the following expressions:

  ```ballerina
  1234
  
  -10
  
  +2020
  ```

### float

The `float` type is an IEEE 64-bit binary (radix 2) floating-point (similar to `double` in Java) and supports the same arithmetic operators as `int`. You have the option to use the `+` or `-` characters in the front of the integer to indicate the sign.

###### Examples

To initialize a variable of type `float` in Choreo, you can use one of the following expressions:

  ```ballerina
  0.3353
  
  -8593.992
  
  +950.930
  ```

### decimal

The `decimal` type corresponds to a subset of IEEE 754-2008 128-bit decimal (radix 10) floating point numbers.

It supports the same arithmetic operations as `int` and `float`. When entering literals, you can use `d` as the suffix to indicate that the value is a `decimal` value.

This type does not support `infinity`, `NaN`, or subnormals.

The `decimal` type is a separate basic type and is a subtype of `anydata`.

###### Examples:

To initialize a variable of type `decimal` in Choreo, you can use one of the following expressions:

```ballerina
0.33536363363636391745284629571635428

-8593.9926666666666666663333333333

+950.930999999992727272777777777774

1.3636336363639174528462957163542834d
```

### boolean

The boolean data type has one of the two possible values: `true` or `false`.

### string

The `string` type represents an immutable sequence of zero or more Unicode characters. In Ballerina, there is no character type. Instead, a string of length one represents a character. Two string values are considered equal (`==`) if both sequences have the same characters. You can use the `<`, `<=`, `>`, and `>=` operators on string values. These work by comparing code points. Unpaired surrogates are not allowed.

###### Examples

To initialize a variable of type `string` in Choreo, you can use one of the following expressions:
  ```ballerina
  "Ballerina"
  
  "Ballerina is a programming language intended for network-distributed applications."
  ```

### var

Ballerina allows you to declare variables using the `var` keyword. Here, Ballerina infers the type of the declared variable based on the right-hand side of the expression.

###### Examples

To initialize a variable of type `var` in Choreo, you can use one of the following expressions:

```ballerina
5
    
"Hello!"
```

### error

The `error` type represents error values. Error values contain a message of the `string` type, a `cause`(optional),  and additional details about the error. The expressions to initialize a variable of the `error` type need to be entered in the `error("<MESSAGE>")` format.

###### Examples:

To initialize a variable of type `error` in Choreo, you can enter an expression as follows:

```ballerina
error("The item requested is not found.")
```

### any

The `any` type represents any value that is not an error. Values assigned to a variable of `any` type can come from dynamic content such as the request and response message references and reference variable types.

###### Examples:

To initialize a variable of type `any` in Choreo, you can enter expressions as follows:


    ```ballerina
        The prices are as follows:
  
        123
  
        349.76
    ```   

### anydata

The `anydata` type consists of pure values of which the basic type is not `error`.

It is a union of   () | boolean | int | float | decimal | string | xml | anydata[] | map<anydata> | table<map<anydata>>

###### Examples:

To initialize a variable of type `anydata` in Choreo, you can enter expressions as follows:

    ```ballerina
        x1.clone();
        
        (x1 == x2);
    ```

## Operators

### String operators

- Concatenation

    You can use the `+` operator to concatenate strings. The string value can come from a string literal or a variable.

    ###### Example

    To concatenate the string `Hi` and a string variable `name`, you can use the following expression:

    ```ballerina
     "Hi " + name;
    ```

### Arithmetic operators

Arithmetic operators perform addition, subtraction, multiplication, division, and modulo on numeric values. You can use literals or variables of type `int` or `float` as numeric values in Ballerina expressions. Here is a list of the Ballerina arithmetic operators.

- Addition

    You can use the `+` operator to add numbers. 

    ###### Example

    To add an integer literal (`20`) and an integer variable `number`, you can use the following expression:

    ```ballerina
    number + 20;
    ```

- Subtraction 

    You can use the `-` operator to subtract numbers. 

    ###### Example

    To subtract two integers (an integer variable `yearOfBirth` from an integer literal `2022`), you can use the following expression:

    ```ballerina
    2022 - yearOfBirth;
    ```
 
- Division

    You can use the `/` operator to divide numbers. 

    ###### Example

    To divide an integer variable `totalNumberOfStudents` from an integer literal `5`, you can use the following expression:

    ```ballerina
    totalNumberOfStudents / 5; 
    ```
 
- Multiplication

    You can use the `*` operator to multiply numbers. 

    ###### Example

    To multiply two float constants (for example, `60` by `5`), you can use the following expression:

    ```ballerina
    60 * 5;
    ```

- Modulo 

    You can use the  `%` operator to get the remainder of a division. 

    ###### Example

    To get the remainder of the division between an integer variable `n` and an integer literal `3`, you can use the following expression:
  
    ```ballerina
    n % 3
    ```

### Value/type operators

- Equality 

    You can use the equality operators to check the equality of [Ballerina](https://ballerina.io/) basic type literals. These values can come from a literal or a variable. The resulting value from these operators is always of `boolean` type.
    
    Ballerina provides the following equality operators: 
    
    | **Operator**     | **Purpose**                                      |
    |------------------|--------------------------------------------------|
    | `==`             | For deep value equality of `anydata` typed values|
    | `===`            | For reference equality                           |
    | `!=` and `!==`   | For negations.                                   |
    

    ###### Examples

    To compare an integer literal `10` with an integer variable `firstNumber`, you can use the following expression:
    
    ```ballerina
    10 == firstNumber
    ```

    To compare a string variable `username`  with  a string literal `admin`, you can use the following expression:  

    ```ballerina
    username != "admin" 
    ```

- Value comparison

    The following operators are available in the expression editor for value comparisons:
    
    - `<`: Less than
    - `>`: Greater than
    - `<=`: Less than or equal to
    - `>=`: Greater than or equal to

    ###### Examples

    To compare the value of the integer variable `firstNumber` to the integer literal `10`, you can use one of the following expressions:

    ```ballerina
    firstNumber > 10
  
    firstNumber <= 10
    ```

    To compare the value of the string variable `greeting` to the string literal `Hi`, you can use the following expression:
   
    ```ballerina
    greeting == "Hi"    
    ```
    To compare an array variable `arrayWithTwoElements` with an array variable `arrayWithOneElement`, you can use the following expression:
    
     ```ballerina
     arrayWithTwoElements <  arrayWithOneElement
     ```
    
## HTTP 

All the HTTP-related services created in Choreo have a variable called `request` (of type `http:Request`), which developers can use to query information related to the HTTP request. The following topics present a list of such use cases.

### Read a header value in the request
A typical HTTP request contains many headers. These include standard and custom headers. You can use the `request` variable's `getHeader` method to read a specific header value in any expression-enabled input field. Note that the `getHeader` method's return type is `string|error`.

###### Examples

To read the value of an HTTP header `Access-Control-Allow-Origin`, you can use the following expression:

```ballerina
check request.getHeader("Access-Control-Allow-Origin")
```
  
To read the value of an HTTP header by passing a variable that contains the header name `headerName`, you can use the following expression:

```ballerina
check request.getHeader(headerName)
```

### Check whether a header is present in the request

Services or integrations in Choreo sometimes need to check the availability of a header before proceeding to something like reading the header value. You can use the `hasHeader` method in the `request` object for this purpose.

###### Examples

To check if the HTTP header `content-length` exists by passing the header name, you can use the following expression:

```ballerina
request.hasHeader("content-length")
```
  
To check if the HTTP header `content-length` exists by passing the header name in the string variable `contentLength`, you can use the following expression: 

```ballerina
request.hasHeader(contentLength)
```
### Read the JSON payload of the request

JSON is a common content type used for HTTP communication. You can use the `request` variable's `getJsonPayload` method to read the JSON payload sent with the HTTP request. Note that the `getJsonPayload` method's return type is `json|error`.

###### Example

To read the JSON payload of the HTTP request, you. can use the following expression:

```ballerina
check request.getJsonPayload()
```

### Read the text payload of the request

Sometimes, content is sent as a string (plain text) with the HTTP request. You can use the `request` variable's `getTextPayload` method to read the text payload sent with the HTTP request. Note that the `getTextPayload` method's return type is `string|error`.

###### Example

To read the text payload of the HTTP request, you can use the following expression:

```ballerina
check request.getTextPayload()
```

### Read the value of a query parameter 

Query parameters are used in an HTTP request to send additional inputs to consider when processing the request. You can use the `request` variable's `getQueryParamValue` method to read the passed query parameter.

###### Examples

To read the value of the query parameter `category`, you can use the following expression:

```ballerina
request.getQueryParamValue("category")
```
  
To read the value of the query parameter passing the query parameter name as a variable, you can use the following expression:

```ballerina
request.getQueryParamValue(queryParamName)
```

### Read the cookies available in the request

HTTP cookies keep stateful information against a client. You can use the `request` variable's `getCookies` method to read cookies present in the request as an array.

###### Example

To read all the cookies available in the request, you can use the following expression:

```ballerina
request.getCookies()
```

## JSON

JSON is a textual format for representing a collection of values: a simple value, an array of values, or an object. The. following topics describe these values.

### Simple values in JSON 

Ballerina has a single type named `json` that can represent any JSON value. You can use `json`, a built-in union type in Ballerina, to store the values of the types `nil` (as the null value), `boolean`, `int`, `float`, `decimal`, `string`, `json[]`, and `map<json>`. 

###### Examples

To initialize a variable of type `json`, you can use the following:
```ballerina
"Alex"

5.36
    
false
    
[1, false, null, "foo", {first: "Alex", last: "John"}]
    
{name: "Alex", age: null, marks: {math: 90, language: 95}}
    
null
```

### JSON objects

Ballerina represents JSON objects as maps. You can define a JSON object to hold values of any `json` type (`string`, `number`, `boolean`, `object`, `array`, or `null`). You can use the variable type `json` to declare a  JSON object.

###### Examples


To declare a JSON object, you can use the following:

```ballerina
{name: "apple", color: "red", price: "20.0"}
```

To create an empty JSON object, you can use the following:

```ballerina
{}
```

To cast a `json` type variable `fruitDetail` to a `map<json>` type variable, you can use the following:

```ballerina

<map<json>>fruitDetail
 
```

To access or change a field of the JSON object `fruitDetailObject`, you can use one of the following:

!!! note
    If you define the JSON object using a `json` type variable, you need to cast it to a `map<json>` first.
    
```ballerina
fruitDetailObject["color"] = "orange"; 
   
fruitDetailObject["name"];
```

To create a nested JSON object, you can use the following:

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

### JSON arrays

JSON array values must be of any of the JSON types: `string`, `number`, `boolean`, `object`, `array`, or `null`. You can define a JSON array in one of the following two ways:
   - Using a [`json` type variable](#simple-values-in-JSON).  
   - Selecting the variable **Type** as `other` and `json[]` as the **Other Type** in the variable statement form.
   
You can access JSON array elements using the index.

###### Examples

To declare a JSON array, you can use the following:

```ballerina
[1, false, null, "foo", {first:  "Alex" , last: "John" }]
```
To cast the `json` type variable `personRecord` to a `json[]` type variable, you can use the following:

```ballerina
<json[]> personRecord;
```

To access elements the fourth element in the JSON array `personRecordArray`, you can use one of the following:

!!! note
    If you define the JSON array using a `json` type variable, you need to cast it to a `json[]` type first.
    
```ballerina
personRecordArray[4]; 
```

To add a JSON array in an object literal, you can use the following:

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

To get the length of a JSON array, you can use the following:

```ballerina
personRecordArray.length(); 
```

### JSON access

You can use the field access(.) and optional field access (?.) operators to access the `json` type in Ballerina. In this case, the type checking happens at runtime. The return type of this operation is a union of `json` and `error`. The following topics describe the field access operators.
    
!!! info
    The examples in this section assume that the following `json` type variable `person` is already defined as
    
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

    You can use the field access operator to access a JSON object stored in a `json` type variable. If the value stored in the `json` type variable is not a JSON object or if the key is invalid, the field access operation returns an error at runtime.
   
    ###### Examples

    To access a field, you can use the following:    

    ```ballerina
    check person.fname;
    ```
    To access a field in a nested level, you can use the following:
 
    ```ballerina
    check person.address.city;
    ```

- Optional field access (?.) 

    You can use the optional field access operator to access a JSON object stored in a `json` type variable. If the value stored in the `json` type variable is not null and not a JSON object, optional field access returns an error. If the value is a JSON object and the key is invalid, the field access operation returns a `null` or `()` at runtime.
    
    ###### Example

    ```ballerina
    check person?.address?.city;
    ```

### User-defined type to JSON type conversions

Conversion from a JSON value to JSON format is straightforward. You can convert an application-specific, user-defined subtype of `anydata` to a JSON value using `toJson`.


###### Example

To convert the record `movieTheRevenant` to `json`, you can use the following:

```ballerina
movieTheRevenant.toJson();     
```

### JSON type to user-defined type conversions

You can cast a JSON value to an application-specific, user-defined subtype of `anydata` by using the `cloneWithType` function.

###### Example

To convert the `theRevenantJson` variable of type `json` to the record `Movie`, you can use the following:

```ballerina
check theRevenantJson.cloneWithType(Movie);
```

## XML

Ballerina allows you to consider an XML value as a sequence representing parsed XML, such as the content of an XML element. Variable statements of the `xml` type can pass the following as expressions:

- Elements

    e.g.,
    
    ```ballerina
        xml `<product><name>block</name></product>`
    ```
- processing instruction

    e.g.,
    
    ```ballerina
        xml `?target data?`
    ```
- comment

    e.g.,
    
    ```ballerina
        xml `!--This is a comment--`
    ```

- text

    e.g.,
    
    ```ballerina
        xml `"Dear customer,"`
    ```
