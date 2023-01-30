create table APP.FILE_METADATA
(
    ID BIGINT not null generated always as identity (START WITH 1, INCREMENT BY 1)
        constraint "FILE_METADATA_pk"
        primary key,
    FILE_PATH        VARCHAR(255)                        not null
        unique,
    FILE_SIZE        BIGINT,
    FILE_CHECKSUM    CHAR(64),
    FILE_CREATED_TS  TIMESTAMP                           not null,
    FILE_MODIFIED_TS TIMESTAMP                           not null,
    CREATED_AT       TIMESTAMP default current_timestamp not null,
    UPDATED_AT       TIMESTAMP default current_timestamp not null,
    IS_FOLDER        BOOLEAN   default false             not null
);

create table APP.METDATA_KEYS
(
    KEY_NAME        VARCHAR(255)                        not null
        constraint "METDATA_KEYS_pk"
            primary key,
    KEY_DESCRIPTION clob,
    KEY_TYPE        INTEGER                             not null,
    CREATED_AT      TIMESTAMP default current_timestamp not null,
    UPDATED_AT      TIMESTAMP default current_timestamp not null
);

create table APP.METADATA_RECORDS
(
    ID         BIGINT       not null
        constraint "metadata_records_FILE_METADATA_ID_fk"
            references APP.FILE_METADATA
            on delete cascade,
    KEY_NAME   VARCHAR(255) not null
        constraint "METADATA_RECORDS_METDATA_KEYS_KEY_NAME_fk"
            references APP.METDATA_KEYS
            on delete cascade
);

