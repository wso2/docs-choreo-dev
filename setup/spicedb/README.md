# Setup SPICE DB

## Create SPICE DB schema

### Prerequisites
- [gRPCurl](https://github.com/fullstorydev/grpcurl)

### Steps
1. Export following environment variables with the correct values.

```bash
export TOKEN=<SPICE_DB_TOKEN>
```

2. Run the following command to create the SPICE DB schema.

```bash
./create_spicedb_schema.sh
```
