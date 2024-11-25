rename table player TO player_history;

alter table mafia_game_result
    modify column language varchar(20) NOT NULL COMMENT '게임 방 언어';
alter table mafia_game_result
    modify column draw text NOT NULL COMMENT '그림 데이터';
alter table mafia_game_result
    modify column mafia_answer varchar(50) NULL COMMENT '마피아 입력 단어';

alter table player_history
    add column is_random_matching boolean NULL COMMENT '마피아 입력 단어';
