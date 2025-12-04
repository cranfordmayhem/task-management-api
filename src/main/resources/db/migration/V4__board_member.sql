CREATE TABLE board_members (
    board_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'MEMBER',
    added_at TIMESTAMP NOT NULL DEFAULT NOW(),

    PRIMARY KEY (board_id, user_id),

    CONSTRAINT fk_board_member_board
        FOREIGN KEY (board_id) REFERENCES board(id) ON DELETE CASCADE,

    CONSTRAINT fk_board_member_user
        FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE
);