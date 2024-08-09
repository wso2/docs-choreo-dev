# Managed Authentication Performace Test Scripts

## Setup

1. Create & deploy Services as required.

2. Create & deploy Webapps as required.

3. Create connections from Webapps to Services.

4. Add users to the Built-in STS 

Note 1: The scripts expect all users to have the same password (purely for convenience reasons). That password has to be specified in `Post Login Form Data` HTTP request in JMeter script. If you need to use different passwords, add it to user data files (see step 5).

Note 2: The login flow of the scripts is tailored for Built-in STS. If an external Identity provider is used, the login flow should be modified appropriately.

5. Create user data files. 

For each thread (i.e. user), create a CSV file with the user specific variables and add all file to a single directory. Filenames must be suffixed with an integer. Thread `n` will use the file whose name ends in `n`.

Eg: `user-data/user5.csv` will be used by `thread 5`.

The test scripts currently expects the following values. If you need to add any other user specific variables (eg: password) it should also go here.
  
  Eg: `user1.csv`
  ```
web_app_domain,sts_domain,api_path,user_name
7c672baf-554e-4203-9305-0b882a048b19.choreoapps.dev,7375bc81-330f-47f2-8e8b-62ee5105dfce-dev.choreosts.dev,/choreo-apis/svc/v1/greet,user1
  ```

6. Set duration of the `Flow Control Action` in JMeter script to a value higher than user token expiry value.


## Run tests

```sh
jmeter -n -t [test-plan-file] -l [results-file] -Jthread_count [thread-count] -Juser_data_file_prefix [path-to-user-data-file-excluding-the-number]
```

eg:

```sh
jmeter -n -t automatic-token-refresh.jmx -l results.jtl -Jthread_count 10 -Juser_data_file_prefix user-data/user
```





