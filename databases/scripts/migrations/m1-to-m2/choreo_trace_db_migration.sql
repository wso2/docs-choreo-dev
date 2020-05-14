
ALTER TABLE `trace_spans` MODIFY COLUMN `invocationFQN` varchar(255);

ALTER TABLE `trace_spans` ADD COLUMN `module` varchar(255), ADD COLUMN `position` varchar(255);
