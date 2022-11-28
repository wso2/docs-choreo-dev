# Connect with Protected Third Party Applications

When your Choreo component connects with protected third-party applications, the firewalls of these applications might block the requests of the Choreo component. To address this, you can add the following public IP ranges of Choreo that it assigns to Choreo components as client IPs to the allow list:

- 20.22.170.144/28
- 20.22.170.176/28

You need to add these IP ranges to the allow list when you develop components that connect to external databases (e.g., MySQL, MSSQL, PGSQL, Oracle DB, etc.) via connectors (e.g., MySQL DB connector). Doing so allows Choreo to create a secure connection.
