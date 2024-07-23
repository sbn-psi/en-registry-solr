/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package gov.nasa.pds.harvest.search.oodt.filemgr;

import gov.nasa.pds.harvest.search.oodt.filemgr.exceptions.VersioningException;
import gov.nasa.pds.harvest.search.oodt.metadata.Metadata;
import gov.nasa.pds.harvest.search.oodt.structs.Product;

/**
 * @author mattmann
 * @version $Revision$
 * 
 *          <p>
 *          This interface defines a versioning scheme for generating the DataStore references for
 *          the items in a {@link Product}.
 *          </p>
 * 
 */
public interface Versioner {

  String X_POINT_ID = Versioner.class.getName();

  void createDataStoreReferences(Product product, Metadata metadata) throws VersioningException;

}
