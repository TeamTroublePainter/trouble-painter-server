alter table player_history
    drop column is_random_matching;

alter table mafia_game_result
    add column is_random_matching boolean NULL COMMENT '마피아 입력 단어';
