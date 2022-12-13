IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='last_update_times' and xtype='U')
BEGIN
    CREATE TABLE last_update_times (
        org_id VARCHAR(128) NOT NULL,
        last_update_time datetime NOT NULL,
        PRIMARY KEY (org_id)
    );
END
GO
