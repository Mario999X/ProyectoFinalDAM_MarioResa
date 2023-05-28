-- TABLES --
CREATE TABLE IF NOT EXISTS Users(
    Id UUID NOT NULL DEFAULT GEN_RANDOM_UUID() UNIQUE,
    Username VARCHAR(255) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Email VARCHAR(255) NOT NULL UNIQUE,
    Role VARCHAR(50) NOT NULL,
    CreatedAt DATE NOT NULL,
    PRIMARY KEY (Id)
);

CREATE TABLE IF NOT EXISTS Scores(
    Id UUID NOT NULL DEFAULT GEN_RANDOM_UUID() UNIQUE,
    UserId UUID NOT NULL,
    ScoreNumber BIGINT NOT NULL,
    DateObtained DATE NOT NULL,
    PRIMARY KEY (Id),
    FOREIGN KEY (UserId) REFERENCES Users(Id)
);