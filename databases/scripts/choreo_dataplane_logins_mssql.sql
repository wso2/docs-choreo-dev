IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_rudder_admin_db_user')
CREATE LOGIN choreo_rudder_admin_db_user with password = N'password'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_mizzen_admin_db_user')
CREATE LOGIN choreo_mizzen_admin_db_user with password = N'password'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_cloud_manager_admin_db_user')
CREATE LOGIN choreo_cloud_manager_admin_db_user with password = N'password'
GO

IF NOT EXISTS
	(SELECT name FROM master.sys.sql_logins
	WHERE name = 'choreo_cicd_admin_db_user')
CREATE LOGIN choreo_cicd_admin_db_user with password = N'password'
GO
