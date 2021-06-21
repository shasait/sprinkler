/*
 * Copyright (C) 2021 by Sebastian Hasait (sebastian at hasait dot de)
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

package de.hasait.sprinkler.service.base;

import javax.annotation.Nonnull;

import com.google.common.base.Joiner;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class MapDbService {

    private static final Logger LOG = LoggerFactory.getLogger(MapDbService.class);

    private final DB db;

    public MapDbService(MapDbServiceConfiguration configuration) {
        super();

        this.db = DBMaker.fileDB(configuration.getPath()).closeOnJvmShutdown().transactionEnable().checksumHeaderBypass().make();

        if (LOG.isInfoEnabled()) {
            LOG.info("AllNames: {}", Joiner.on(", ").join(db.getAllNames()));
        }
    }

    public void commit() {
        db.commit();
    }

    @Nonnull
    public DB getDb() {
        return db;
    }

}
