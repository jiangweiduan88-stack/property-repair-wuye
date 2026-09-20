-- 为房屋表补充业主姓名快照，并使用现有用户昵称回填历史房屋数据。
ALTER TABLE prop_room
    ADD COLUMN owner_name varchar(64) DEFAULT '' COMMENT 'owner name' AFTER owner_id;

UPDATE prop_room r
LEFT JOIN sys_user u ON u.user_id = r.owner_id
SET r.owner_name = COALESCE(u.nick_name, '')
WHERE r.owner_name IS NULL OR r.owner_name = '';
