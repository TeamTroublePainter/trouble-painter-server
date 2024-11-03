alter table mafia_game_result
    modify column game_result text NULL;
alter table mafia_game_result
    add column language varchar(20) NOT NULL;
alter table mafia_game_result
    add column draw text NOT NULL;
alter table mafia_game_result
    add column mafia_answer varchar(50) NULL;

create table player
(
    player_id            bigint      NOT NULL AUTO_INCREMENT COMMENT 'PK',
    result               varchar(20) NOT NULL COMMENT '게임 승리 여부',
    role                 varchar(20) NOT NULL COMMENT '플레이어 역할',
    created_at           datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    user_id              bigint      NOT NULL COMMENT 'FK - Users',
    mafia_game_result_id bigint      NOT NULL COMMENT 'FK - MafiaGameResult',
    PRIMARY KEY PK_player (player_id),
    KEY IDX_createdat (created_at),
    KEY IDX_updatedat (updated_at)
) COMMENT '플레이어 정보';
