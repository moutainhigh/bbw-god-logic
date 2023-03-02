ALTER TABLE `god_game`.`ins_role_info` 
ADD COLUMN `login_times` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '登录次数' AFTER `row_update_time`,
ADD COLUMN `pay` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '累积充值' AFTER `login_times`;

区服
ALTER TABLE `ins_receipt` ADD COLUMN `gmop` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '0正常订单，1管理员操作' AFTER `dispatch_golds`;