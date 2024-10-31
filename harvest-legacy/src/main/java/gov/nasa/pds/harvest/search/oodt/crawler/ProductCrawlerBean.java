package gov.nasa.pds.harvest.search.oodt.crawler;
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

// JDK imports
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
// Spring imports
import org.springframework.context.ApplicationContext;
import gov.nasa.pds.harvest.search.crawler.metadata.PDSCoreMetKeys;
import gov.nasa.pds.harvest.search.oodt.metadata.Metadata;

/**
 * Turns a {@link ProductCrawler} into a Spring-configurable entity.
 *
 * @author bfoster (Brian Foster)
 */
public abstract class ProductCrawlerBean implements PDSCoreMetKeys {

  private HashSet<String> requiredMetadata;

  private List<String> actionIds;

  private String filemgrUrl;

  private String clientTransferer;

  private boolean noRecur, crawlForDirs, skipIngest;

  private int daemonWait, daemonPort;

  private String productPath;

  private ApplicationContext applicationContext;

  private String id;

  private Metadata globalMetadata;

  public ProductCrawlerBean() {
    this.actionIds = new LinkedList<String>();
    this.requiredMetadata = new HashSet<String>();
    this.requiredMetadata.add(LOGICAL_ID);
    this.requiredMetadata.add(PRODUCT_VERSION);
    this.requiredMetadata.add(TITLE);
    this.noRecur = false;
    this.crawlForDirs = false;
    this.skipIngest = true;
    this.daemonPort = -1;
    this.daemonWait = -1;
    this.globalMetadata = new Metadata();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  public ApplicationContext getApplicationContext() {
    return this.applicationContext;
  }

  public void setActionIds(List<String> actionIds) {
    this.actionIds = actionIds;
  }

  public List<String> getActionIds() {
    return this.actionIds;
  }

  public void setRequiredMetadata(List<String> requiredMetadata) {
    this.requiredMetadata.addAll(requiredMetadata);
  }

  public void setFilemgrUrl(String filemgrUrl) throws MalformedURLException {
    this.filemgrUrl = filemgrUrl;
  }

  public String getFilemgrUrl() {
    return this.filemgrUrl;
  }

  public List<String> getRequiredMetadata() {
    return new LinkedList<String>(this.requiredMetadata);
  }

  public void setClientTransferer(String clientTransferer) {
    this.clientTransferer = clientTransferer;
  }

  public String getClientTransferer() {
    return this.clientTransferer;
  }

  public void setNoRecur(boolean noRecur) {
    this.noRecur = noRecur;
  }

  public boolean isNoRecur() {
    return this.noRecur;
  }

  public void setCrawlForDirs(boolean crawlForDirs) {
    this.crawlForDirs = crawlForDirs;
  }

  public boolean isCrawlForDirs() {
    return this.crawlForDirs;
  }

  public void setSkipIngest(boolean skipIngest) {
    this.skipIngest = skipIngest;
  }

  public boolean isSkipIngest() {
    return this.skipIngest;
  }

  public void setDaemonWait(int daemonWait) {
    this.daemonWait = daemonWait;
  }

  public int getDaemonWait() {
    return this.daemonWait;
  }

  public void setDaemonPort(int daemonPort) {
    this.daemonPort = daemonPort;
  }

  public int getDaemonPort() {
    return this.daemonPort;
  }

  public void setProductPath(String productPath) {
    this.productPath = productPath;
  }

  public String getProductPath() {
    return this.productPath;
  }

  public Metadata getGlobalMetadata() {
    return globalMetadata;
  }

  public void setGlobalMetadata(Metadata globalMetadata) {
    this.globalMetadata.addMetadata(globalMetadata.getMap());
  }

}
