-- Create Logins on the MSSQL Server

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_apim_db_user')
CREATE LOGIN choreo_apim_db_user with password = N'xxxxxxxxxxxxx'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_apim_shared_db_user')
CREATE LOGIN choreo_apim_shared_db_user with password = N'xxxxxxxxxxxxx'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_apim_user_db_user')
CREATE LOGIN choreo_apim_user_db_user with password = N'xxxxxxxxxxxxx'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_app_db_user')
CREATE LOGIN choreo_app_db_user with password = N'xxxxxxxxxxxxx'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_cicd_admin_db_user')
CREATE LOGIN choreo_cicd_admin_db_user with password = N'xxxxxxxxxxxxx'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_cloud_manager_admin_db_user')
CREATE LOGIN choreo_cloud_manager_admin_db_user with password = N'xxxxxxxxxxxxx'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_global_adapter_db_user')
CREATE LOGIN choreo_global_adapter_db_user with password = N'xxxxxxxxxxxxx'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_mizzen_admin_db_user')
CREATE LOGIN choreo_mizzen_admin_db_user with password = N'xxxxxxxxxxxxx'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_org_remover_db_user')
CREATE LOGIN choreo_org_remover_db_user with password = N'xxxxxxxxxxxxx'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_perf_db_user')
CREATE LOGIN choreo_perf_db_user with password = N'xxxxxxxxxxxxx'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_program_db_user')
CREATE LOGIN choreo_program_db_user with password = N'xxxxxxxxxxxxx'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_rudder_admin_db_user')
CREATE LOGIN choreo_rudder_admin_db_user with password = N'xxxxxxxxxxxxx'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_step_db_user')
CREATE LOGIN choreo_step_db_user with password = N'xxxxxxxxxxxxx'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_subscriptions_db_user')
CREATE LOGIN choreo_subscriptions_db_user with password = N'xxxxxxxxxxxxx'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_testbase_db_user')
CREATE LOGIN choreo_testbase_db_user with password = N'xxxxxxxxxxxxx'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'billing_db_user')
CREATE LOGIN billing_db_user with password = N'xxxxxxxxxxxxx'
GO

--IF NOT EXISTS
--	(SELECT name FROM master.sys.sql_logins
--	WHERE name = 'choreo-aiexp-db-reader')
--CREATE LOGIN choreo-aiexp-db-reader with password = N'xxxxxxxxxxxxx'
--GO
