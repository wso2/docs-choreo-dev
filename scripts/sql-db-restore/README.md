### Restoring Multiple Azure SQL DB Instances

This script is designed to restore multiple databases simultaneously.

Before executing this script make sure to install the jq package. You can install it with following commands
```
# Mac using homebrew
brew install jq

# Linux, Debian
sudo apt install jq

# Windows using Chocolatey
choco install jq
```

Before running this script, ensure that the following variable values are replaced with the appropriate information:

```
db_names_to_restore=()

Add the database names which needs to be restored to the above array
Ex: db_names_to_restore=("test_db_1" "test_db_2" "test_db_3")
```

```
resource_group=""
server_name=""
subscription_id=""
elastic_pool=""
restore_time=""

Change the above variables with the correct values

Ex: 

resource_group="choreo_rg"  # The resource group where the databases are included

server_name="sql-server-name" # The SQL server name where the databases are included

subscription_id="sub id of account" # Subscription id 

elastic_pool="elastic pool of the databases" # Elastic pool where the databases are included

restore_time="time of the restore time" # The time frame where the DBs needed to be restored. This needs to be in the format of "2018-05-20T05:34:22"
```
