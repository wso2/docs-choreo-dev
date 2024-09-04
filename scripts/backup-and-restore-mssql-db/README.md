## Bash scripts to export and import MSSQL databases

In here there are several bash scripts have been implemented to export and import MSSQL server databases. Here the database snapshots are exported in to Azure storage accounts by creating seperate container for each of the databases. This main storage account needs to be created previously by manaully.

### Change the values in credentials.sh

Before executing any script we need to change the values in this ```credentials.sh``` file.
```
export resourceGroup=""             # Resource group name 
export storage=""                   # Storage account name
export subscriptionId=""            # Subscription id 
export password=""                  # Password of the MSSQL sever
export login=""                     # Username of the MSSQL server
export server=""                    # Name of the MSSQL server

export elasticPool=""               # Name of the elastic pool where the databases needed to be export and import

currentDate="$(date '+%Y-%m-%d')"
export currentDate
export dateToRestore="2024-08-27"   # Date of the snapshots where the dbs needed to be imported

export bacpac="${currentDate}-backup.bacpac" # Name of the exported file of the database

export data_delete_date="2024-08-28" # Date which the previous snapshots needed to be deleted. Ex: If we need to delete 2024-08-27 data then we need assign "2024-08-28" to this variable.
```

#### Exporting the databases to a storage account

Then to export the databases ```container-setup-and-export.sh``` script needed to be executed. 
After executing the command, it will create the containers (if the containers are not exists already) inside the storage account for each of the databases. And then it exports the database snapshots to the created container with ```.bacpac``` format. Here the exporting happens in parallel.

After exporting the data we can validate the datbase export by executing the ```db-export-check.sh``` script. It validates the export process of each of the databases and printed out the databases which are have not correctly exported.

### Importing the data

To import the data and create the necessary databases and the elastic pool, ```create-db-and-import.sh``` script needs to be executed. This will create the elastic pool with the given name and create relavent databases inside it. Then it imports the previously exported data from the storage account.

### Deleting the previous data

```delete-data.sh``` script can be used to delete the exported data corresponding with a relavent date.