begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|//package org.apache.solr.analysis;
end_comment

begin_comment
comment|//import org.apache.lucene.analysis.ru.*;
end_comment

begin_comment
comment|//import java.util.Map;
end_comment

begin_comment
comment|//import java.util.HashMap;
end_comment

begin_comment
comment|//import org.apache.solr.core.SolrConfig;
end_comment

begin_comment
comment|//import org.apache.solr.common.SolrException;
end_comment

begin_comment
comment|//import org.apache.solr.common.SolrException.ErrorCode;
end_comment

begin_comment
comment|//import org.slf4j.Logger;
end_comment

begin_comment
comment|//import org.slf4j.LoggerFactory;
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//@Deprecated
end_comment

begin_comment
comment|//public class RussianCommon {
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  private static Logger logger = LoggerFactory.getLogger(RussianCommon.class);
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  private static Map<String,char[]> CHARSETS = new HashMap<String,char[]>();
end_comment

begin_comment
comment|//  static {
end_comment

begin_comment
comment|//    CHARSETS.put("UnicodeRussian",RussianCharsets.UnicodeRussian);
end_comment

begin_comment
comment|//    CHARSETS.put("KOI8",RussianCharsets.KOI8);
end_comment

begin_comment
comment|//    CHARSETS.put("CP1251",RussianCharsets.CP1251);
end_comment

begin_comment
comment|//  }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  public static char[] getCharset(String name) {
end_comment

begin_comment
comment|//    if (null == name)
end_comment

begin_comment
comment|//      return RussianCharsets.UnicodeRussian;
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    char[] charset = CHARSETS.get(name);
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    if (charset.equals(RussianCharsets.UnicodeRussian))
end_comment

begin_comment
comment|//      logger.warn("Specifying UnicodeRussian is no longer required (default).  "
end_comment

begin_comment
comment|//          + "Use of the charset parameter will cause an error in Solr 1.5");
end_comment

begin_comment
comment|//    else
end_comment

begin_comment
comment|//      logger.warn("Support for this custom encoding is deprecated.  "
end_comment

begin_comment
comment|//          + "Use of the charset parameter will cause an error in Solr 1.5");
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    if (null == charset) {
end_comment

begin_comment
comment|//      throw new SolrException(ErrorCode.SERVER_ERROR,
end_comment

begin_comment
comment|//                              "Don't understand charset: " + name);
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//    return charset;
end_comment

begin_comment
comment|//  }
end_comment

begin_comment
comment|//}
end_comment

end_unit

