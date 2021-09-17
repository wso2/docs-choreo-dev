-- Create multivariant anomaly detector alert table

IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[MULTIVARIANT_ANOMALY_ALERTS]') AND TYPE IN (N'U'))
CREATE TABLE MULTIVARIANT_ANOMALY_ALERTS  
(  
 alert_id int IDENTITY(1,1) NOT NULL PRIMARY KEY,  
 obs_id varchar (100) NOT NULL,  
 obs_version varchar(100) NOT NULL,  
 node_id varchar(100) NOT NULL,  
 confidence float NOT NULL,  
 triggered_timestamp datetime2 NOT NULL,  
 last_occurrence_timestamp datetime2 NOT NULL,  
 notification_opened bit NOT NULL,
 anomaly_count smallint NOT NULL  
);
