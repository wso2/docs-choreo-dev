# Choreo token scope configuration

The scopes that are required to be sent in the token request calls are declared here. 
The scope values can vary across Choreo environments, therefore environment specific 
yaml files are maintained with the respective scope lists.

Since there are a large number of scopes that are being updated from time to time, the `gen-scopes-ymal.py` script
can be used to automatically generate the respective scopes yaml file by extracting the scopes from a Bearer token
issued by a given Choreo environment. This will ensure that the scopes list can be updated easily. 

## How to update the scopes list

1. Login to a given Choreo environment and obtain the base64 encoded STS Bearer token from the browser console.


2. Pass the token as a command line argument to the `gen-scopes-yaml.py` script along with the respective 
   Choreo environment string(**dev**, **stage** or **prod**) and execute as follows,

    ```
    ./gen-scopes-yaml.py -t <TOKEN> -e <dev|stage|prod>
    ```    
   
    #### Example: 
    ```
    ./gen-scopes-yaml.py -t eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c -e dev
    ```


3. Executing the above will env specific scope list yaml file to get overwritten by the scope values that were found in
   the provided token.


4. Remember to commit any changes that are seen in the respective scope yaml files to git
