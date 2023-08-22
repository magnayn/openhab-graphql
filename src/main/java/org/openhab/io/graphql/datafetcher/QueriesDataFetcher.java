/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.io.graphql.datafetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

/**
 * Handles Hue scenes via the automation subsystem and the corresponding REST interface
 *
 * @author Nigel Magnay - Initial contribution
 */
// @QueryMapping(fieldName = "price")
public class QueriesDataFetcher implements DataFetcher {
    private final Logger logger = LoggerFactory.getLogger(QueriesDataFetcher.class);

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        return "1";
    }
}
