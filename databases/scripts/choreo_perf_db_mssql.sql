-- Create User
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_perf_db_user')
BEGIN
    CREATE USER [choreo_perf_db_user] FOR LOGIN [choreo_perf_db_user]
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_perf_db TO choreo_perf_db_user
END;
GO

CREATE TABLE [dbo].[api_metrics](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[time_stamp] [bigint] NOT NULL,
	[api_name] [varchar](256) NOT NULL,
	[wip] [real] NOT NULL,
	[latency] [real] NOT NULL,
	[throughput] [real] NOT NULL,
 CONSTRAINT [PK_api_metrics_id] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ml_models]    Script Date: 9/21/2021 5:19:18 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ml_models](
	[api_id] [int] IDENTITY(1,1) NOT NULL,
	[api_name] [varchar](256) NOT NULL,
	[data_min] [real] NOT NULL,
	[data_max] [real] NOT NULL,
	[version] [bigint] NOT NULL,
	[avg_process_time] [real] NOT NULL,
	[max_process_time] [real] NOT NULL,
	[min_process_time] [real] NOT NULL,
 CONSTRAINT [PK_ml_models_api_id] PRIMARY KEY CLUSTERED
(
	[api_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [ml_models$api_name_unique] UNIQUE NONCLUSTERED
(
	[api_name] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[model_store]    Script Date: 9/21/2021 5:19:18 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[model_store](
	[api_id] [int] NOT NULL,
	[model_type] [smallint] NOT NULL,
	[model] [varbinary](max) NOT NULL,
	[model_score] [real] NOT NULL,
 CONSTRAINT [PK_model_store_api_id] PRIMARY KEY CLUSTERED
(
	[api_id] ASC,
	[model_type] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
ALTER TABLE [dbo].[model_store] ADD  DEFAULT ((1)) FOR [model_type]
GO
ALTER TABLE [dbo].[model_store]  WITH NOCHECK ADD  CONSTRAINT [model_store$model_store_ibfk_1] FOREIGN KEY([api_id])
REFERENCES [dbo].[ml_models] ([api_id])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[model_store] CHECK CONSTRAINT [model_store$model_store_ibfk_1]
GO
