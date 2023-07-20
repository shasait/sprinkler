insert into RELAY (ID, VERSION, NAME, PROVIDER_ID, PROVIDER_CONFIG) values (NEXT VALUE FOR RELAY_SEQ, 1, 'R1 - Drip line',    'sysclass-gpio',  '4');
insert into RELAY (ID, VERSION, NAME, PROVIDER_ID, PROVIDER_CONFIG) values (NEXT VALUE FOR RELAY_SEQ, 1, 'R2 - Garden front', 'sysclass-gpio', '22');
insert into RELAY (ID, VERSION, NAME, PROVIDER_ID, PROVIDER_CONFIG) values (NEXT VALUE FOR RELAY_SEQ, 1, 'R3 - Garden rear',  'sysclass-gpio',  '6');
insert into RELAY (ID, VERSION, NAME, PROVIDER_ID, PROVIDER_CONFIG) values (NEXT VALUE FOR RELAY_SEQ, 1, 'R4 - Unused',       'sysclass-gpio', '26');

insert into SENSOR (
    ID, VERSION,
    NAME,
    PROVIDER_ID, PROVIDER_CONFIG,
    CRON_EXPRESSION
) values (
    NEXT VALUE FOR SENSOR_SEQ, 1,
    'Local rain',
    'dummy', '100',
    '0 */10 * * * *'
);

insert into SCHEDULE (
    ID, VERSION,
    ENABLED,
    RELAY_ID,
    DURATION_S,
    SENSOR_ID, SENSOR_INFLUENCE, SENSOR_CHANGE_LIMIT,
    CRON_EXPRESSION
) values (
    NEXT VALUE FOR SCHEDULE_SEQ, 1,
    TRUE,
    select ID from RELAY where NAME = 'R4 - Unused',
    900,
    select ID from SENSOR where NAME = 'Local rain', 50, 10,
    '0 0 5 * * *'
);
