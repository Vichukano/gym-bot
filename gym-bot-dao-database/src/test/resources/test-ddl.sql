CREATE TABLE GYM_BOT_USER(
    ID          INTEGER NOT NULL PRIMARY KEY,
    NAME        TEXT    NOT NULL,
    CREATE_DATE TEXT    NOT NULL,
    LANGUAGE    CHAR(3) NOT NULL,
    NICK_NAME   TEXT,
    UPDATE_DATE TEXT,
    WEIGHT      INTEGER(3),
    HEIGHT      INTEGER(3)
);

CREATE TABLE GYM_BOT_TRAINING(
    ID            INTEGER NOT NULL PRIMARY KEY,
    USER_ID       INTEGER NOT NULL,
    TRAINING_DATE TEXT    NOT NULL,
    TRAINING_TIME TEXT    NOT NULL,
    FOREIGN KEY(USER_ID) REFERENCES GYM_BOT_USER(ID)
);

CREATE TABLE GYM_BOT_EXERCISE(
    ID          INTEGER    NOT NULL PRIMARY KEY,
    TRAINING_ID INTEGER    NOT NULL,
    WEIGHT      INTEGER(3) NOT NULL,
    REPS        INTEGER(3) NOT NULL,
    NAME        TEXT       NOT NULL,
    CREATE_DATE TEXT       NOT NULL,
    FOREIGN KEY(TRAINING_ID) REFERENCES GYM_BOT_TRAINING(ID)
);