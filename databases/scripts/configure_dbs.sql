CREATE USER 'choreo_app_db_user'@'%' IDENTIFIED BY 'pw_app_db_user';
CREATE USER 'choreo_perf_db_user'@'%' IDENTIFIED BY 'pw_perf_db_user';
CREATE USER 'choreo_program_db_user'@'%' IDENTIFIED BY 'pw_program_db_user';

GRANT SELECT, INSERT, UPDATE, DELETE ON `choreo_app_db`.* TO 'choreo_app_db_user'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON `choreo_perf_db`.* TO 'choreo_perf_db_user'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON `choreo_program_db`.* TO 'choreo_program_db_user'@'%';
