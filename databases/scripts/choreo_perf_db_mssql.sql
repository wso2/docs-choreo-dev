-- Create User
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'choreo_perf_db') AND NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_perf_db_user')
BEGIN
    CREATE USER [choreo_perf_db_user] with password = N'xxxxxxxxxxxxx'
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_perf_db TO choreo_perf_db_user
END;
GO

CREATE TABLE [dbo].[api_metrics](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[time_stamp] [bigint] NOT NULL,
	[api_name] [varchar](256) NOT NULL,
	[env_name] [varchar](16),
	[wip] [real] NOT NULL,
	[latency] [real] NOT NULL,
	[throughput] [real] NOT NULL,
	[request_count] [int] NOT NULL,
	[preprocessing_code_version] [decimal](3,2) NOT NULL,
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
	[version_id] [int] NOT NULL,
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
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [ml_models$ml_models_ibfk_1] FOREIGN KEY ([version_id])
 REFERENCES [dbo].[model_version]([version_id])
 ON UPDATE CASCADE
 ON DELETE CASCADE,
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
	[is_enabled] [smallint] NOT NULL,
 CONSTRAINT [PK_model_store_api_id] PRIMARY KEY CLUSTERED
(
	[api_id] ASC,
	[model_type] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
ALTER TABLE [dbo].[model_store] ADD  DEFAULT ((1)) FOR [model_type]
GO
ALTER TABLE [dbo].[model_store] ADD  DEFAULT ((1)) FOR [is_enabled]
GO
ALTER TABLE [dbo].[model_store]  WITH NOCHECK ADD  CONSTRAINT [model_store$model_store_ibfk_1] FOREIGN KEY([api_id])
REFERENCES [dbo].[ml_models] ([api_id])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[model_store] CHECK CONSTRAINT [model_store$model_store_ibfk_1]
GO

CREATE TABLE [dbo].[data_version] (
	[dataset_id] [int] NOT NULL IDENTITY(1,1),
	[api_name] [varchar](256) NOT NULL,
	[start_id] [int] NOT NULL,
	[end_id] [int] NOT NULL,
	[high_wip_count] [int] NOT NULL,
	[low_wip_count]	[int] NOT NULL,
	[versioned_time] [datetime]	NOT NULL,
 CONSTRAINT [PK_data_version_dataset_id] PRIMARY KEY CLUSTERED(
	[dataset_id] ASC
) WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
) ON [PRIMARY];
GO

CREATE TABLE [dbo].[model_version] (
	[version_id] [int] NOT NULL IDENTITY(1,1),
	[dataset_id] [int] NOT NULL,
	[preprocessing_code_version] [decimal](3,2)	NOT NULL,
	[model_code_version] [decimal](3,2)	NOT NULL,
	[created_time] [datetime] NOT NULL,
	[is_data_change] [smallint]	NOT NULL,
 CONSTRAINT [PK_version_version_id] PRIMARY KEY CLUSTERED(
	[version_id] ASC
) WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [model_version$model_version_ibfk_1] FOREIGN KEY (
    [dataset_id]
) REFERENCES [dbo].[data_version]([dataset_id]) ON UPDATE CASCADE ON DELETE CASCADE,
) ON [PRIMARY];
GO

CREATE TABLE [dbo].[metric] (
	[metric_id] [int] NOT NULL IDENTITY(1,1),
	[name] [varchar](20) NOT NULL,
 CONSTRAINT [PK_metric_metric_id] PRIMARY KEY CLUSTERED(
	[metric_id] ASC
) WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
) ON [PRIMARY];
GO

CREATE TABLE [dbo].[model_score] (
	[score_id] [int] NOT NULL IDENTITY (1,1),
	[version_id] [int] NOT NULL,
	[metric_id]	[int] NOT NULL,
	[model_type] [smallint]	NOT NULL,
	[score]	[real] NOT NULL,
	[score_min]	[real] NOT NULL,
	[score_max]	[real] NOT NULL,
 CONSTRAINT [PK_model_score_id] PRIMARY KEY CLUSTERED(
	[score_id] 	ASC
) WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [model_score$model_score_ibfk_1] FOREIGN KEY (
    [version_id]
) REFERENCES [dbo].[model_version]([version_id]) ON UPDATE CASCADE ON DELETE CASCADE,
 CONSTRAINT [model_score$model_score_ibfk_2] FOREIGN KEY (
    [metric_id]
) REFERENCES [dbo].[metric]([metric_id]) ON UPDATE CASCADE ON DELETE CASCADE,
) ON [PRIMARY];
GO

CREATE TABLE [dbo].[model_feedback] (
	[feedback_id] [int]	NOT NULL IDENTITY(1,1),
	[dataset_id] [int],
	[version_id] [int] NOT NULL,
	[metric_id]	[int] NOT NULL,
	[model_type] [smallint]	NOT NULL,
	[score]	[real] NOT NULL,
	[is_before_train] [smallint] NOT NULL,
CONSTRAINT [PK_model_feedback_id] PRIMARY KEY CLUSTERED(
	[feedback_id] 	ASC
) WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [model_feedback$model_feedback_ibfk_1] FOREIGN KEY (
    [dataset_id]
) REFERENCES [dbo].[data_version]([dataset_id]) ON UPDATE NO ACTION ON DELETE NO ACTION,
CONSTRAINT [model_feedback$model_feedback_ibfk_2] FOREIGN KEY (
    [version_id]
) REFERENCES [dbo].[model_version]([version_id]) ON UPDATE CASCADE ON DELETE CASCADE,
CONSTRAINT [model_feedback$model_feedback_ibfk_3] FOREIGN KEY (
    [metric_id]
) REFERENCES [dbo].[metric]([metric_id]) ON UPDATE CASCADE ON DELETE CASCADE,
) ON [PRIMARY];
GO
