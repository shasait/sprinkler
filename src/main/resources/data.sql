/*
 * Copyright (C) 2026 by Sebastian Hasait (sebastian at hasait dot de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

insert into RELAY (ID, VERSION, NAME, DRIVER_ID, DRIVER_CONFIG) values (NEXT VALUE FOR RELAY_SEQ, 1, 'R1 - Drip line',    'sysclass-gpio',  '4');
insert into RELAY (ID, VERSION, NAME, DRIVER_ID, DRIVER_CONFIG) values (NEXT VALUE FOR RELAY_SEQ, 1, 'R2 - Garden front', 'sysclass-gpio', '22');
insert into RELAY (ID, VERSION, NAME, DRIVER_ID, DRIVER_CONFIG) values (NEXT VALUE FOR RELAY_SEQ, 1, 'R3 - Garden rear',  'sysclass-gpio',  '6');
insert into RELAY (ID, VERSION, NAME, DRIVER_ID, DRIVER_CONFIG) values (NEXT VALUE FOR RELAY_SEQ, 1, 'R4 - Unused',       'sysclass-gpio', '26');

insert into SENSOR (
    ID, VERSION,
    NAME,
    DRIVER_ID, DRIVER_CONFIG,
    SCHEDULE_ENABLED, SCHEDULE_CRON
) values (
    NEXT VALUE FOR SENSOR_SEQ, 1,
    'Local rain',
    'dummy', '100+-10',
    TRUE, '*/10 * * * * *'
);

insert into SCHEDULE (
    ID, VERSION,
    RELAY_ID,
    DURATION_S,
    SENSOR_ID, SENSOR_INFLUENCE, SENSOR_CHANGE_LIMIT,
    SCHEDULE_ENABLED, SCHEDULE_CRON
) values (
    NEXT VALUE FOR SCHEDULE_SEQ, 1,
    select ID from RELAY where NAME = 'R4 - Unused',
    900,
    select ID from SENSOR where NAME = 'Local rain', 50, 10,
    TRUE, '0 0 5 * * *'
);
