begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package

begin_interface
DECL|interface|LangIdParams
specifier|public
interface|interface
name|LangIdParams
block|{
DECL|field|LANGUAGE_ID
name|String
name|LANGUAGE_ID
init|=
literal|"langid"
decl_stmt|;
DECL|field|DOCID_PARAM
name|String
name|DOCID_PARAM
init|=
name|LANGUAGE_ID
operator|+
literal|".idField"
decl_stmt|;
DECL|field|FIELDS_PARAM
name|String
name|FIELDS_PARAM
init|=
name|LANGUAGE_ID
operator|+
literal|".fl"
decl_stmt|;
comment|// Field list to detect from
DECL|field|LANG_FIELD
name|String
name|LANG_FIELD
init|=
name|LANGUAGE_ID
operator|+
literal|".langField"
decl_stmt|;
comment|// Main language detected
DECL|field|LANGS_FIELD
name|String
name|LANGS_FIELD
init|=
name|LANGUAGE_ID
operator|+
literal|".langsField"
decl_stmt|;
comment|// All languages detected (multiValued)
DECL|field|FALLBACK
name|String
name|FALLBACK
init|=
name|LANGUAGE_ID
operator|+
literal|".fallback"
decl_stmt|;
comment|// Fallback lang code
DECL|field|FALLBACK_FIELDS
name|String
name|FALLBACK_FIELDS
init|=
name|LANGUAGE_ID
operator|+
literal|".fallbackFields"
decl_stmt|;
comment|// Comma-sep list of fallback fields
DECL|field|OVERWRITE
name|String
name|OVERWRITE
init|=
name|LANGUAGE_ID
operator|+
literal|".overwrite"
decl_stmt|;
comment|// Overwrite if existing language value in LANG_FIELD
DECL|field|THRESHOLD
name|String
name|THRESHOLD
init|=
name|LANGUAGE_ID
operator|+
literal|".threshold"
decl_stmt|;
comment|// Detection threshold
DECL|field|ENFORCE_SCHEMA
name|String
name|ENFORCE_SCHEMA
init|=
name|LANGUAGE_ID
operator|+
literal|".enforceSchema"
decl_stmt|;
comment|// Enforces that output fields exist in schema
DECL|field|LANG_WHITELIST
name|String
name|LANG_WHITELIST
init|=
name|LANGUAGE_ID
operator|+
literal|".whitelist"
decl_stmt|;
comment|// Allowed languages
DECL|field|LCMAP
name|String
name|LCMAP
init|=
name|LANGUAGE_ID
operator|+
literal|".lcmap"
decl_stmt|;
comment|// Maps detected langcode to other value
DECL|field|MAP_ENABLE
name|String
name|MAP_ENABLE
init|=
name|LANGUAGE_ID
operator|+
literal|".map"
decl_stmt|;
comment|// Turns on or off the field mapping
DECL|field|MAP_FL
name|String
name|MAP_FL
init|=
name|LANGUAGE_ID
operator|+
literal|".map.fl"
decl_stmt|;
comment|// Field list for mapping
DECL|field|MAP_OVERWRITE
name|String
name|MAP_OVERWRITE
init|=
name|LANGUAGE_ID
operator|+
literal|".map.overwrite"
decl_stmt|;
comment|// Whether to overwrite existing fields
DECL|field|MAP_KEEP_ORIG
name|String
name|MAP_KEEP_ORIG
init|=
name|LANGUAGE_ID
operator|+
literal|".map.keepOrig"
decl_stmt|;
comment|// Keep original field after mapping
DECL|field|MAP_INDIVIDUAL
name|String
name|MAP_INDIVIDUAL
init|=
name|LANGUAGE_ID
operator|+
literal|".map.individual"
decl_stmt|;
comment|// Detect language per individual field
DECL|field|MAP_INDIVIDUAL_FL
name|String
name|MAP_INDIVIDUAL_FL
init|=
name|LANGUAGE_ID
operator|+
literal|".map.individual.fl"
decl_stmt|;
comment|// Field list of fields to redetect language for
DECL|field|MAP_LCMAP
name|String
name|MAP_LCMAP
init|=
name|LANGUAGE_ID
operator|+
literal|".map.lcmap"
decl_stmt|;
comment|// Enables mapping multiple langs to same output field
DECL|field|MAP_PATTERN
name|String
name|MAP_PATTERN
init|=
name|LANGUAGE_ID
operator|+
literal|".map.pattern"
decl_stmt|;
comment|// RegEx pattern to match field name
DECL|field|MAP_REPLACE
name|String
name|MAP_REPLACE
init|=
name|LANGUAGE_ID
operator|+
literal|".map.replace"
decl_stmt|;
comment|// Replace pattern
DECL|field|MAX_FIELD_VALUE_CHARS
name|String
name|MAX_FIELD_VALUE_CHARS
init|=
name|LANGUAGE_ID
operator|+
literal|".maxFieldValueChars"
decl_stmt|;
comment|// Maximum number of characters to use per field for language detection
DECL|field|MAX_TOTAL_CHARS
name|String
name|MAX_TOTAL_CHARS
init|=
name|LANGUAGE_ID
operator|+
literal|".maxTotalChars"
decl_stmt|;
comment|// Maximum number of characters to use per all concatenated fields for language detection
DECL|field|DOCID_FIELD_DEFAULT
name|String
name|DOCID_FIELD_DEFAULT
init|=
literal|"id"
decl_stmt|;
DECL|field|DOCID_LANGFIELD_DEFAULT
name|String
name|DOCID_LANGFIELD_DEFAULT
init|=
literal|null
decl_stmt|;
DECL|field|DOCID_LANGSFIELD_DEFAULT
name|String
name|DOCID_LANGSFIELD_DEFAULT
init|=
literal|null
decl_stmt|;
DECL|field|MAP_PATTERN_DEFAULT
name|String
name|MAP_PATTERN_DEFAULT
init|=
literal|"(.*)"
decl_stmt|;
DECL|field|MAP_REPLACE_DEFAULT
name|String
name|MAP_REPLACE_DEFAULT
init|=
literal|"$1_{lang}"
decl_stmt|;
DECL|field|MAX_FIELD_VALUE_CHARS_DEFAULT
name|int
name|MAX_FIELD_VALUE_CHARS_DEFAULT
init|=
literal|10000
decl_stmt|;
DECL|field|MAX_TOTAL_CHARS_DEFAULT
name|int
name|MAX_TOTAL_CHARS_DEFAULT
init|=
literal|20000
decl_stmt|;
comment|// TODO: This default threshold accepts even "uncertain" detections.
comment|// Increase&langid.threshold above 0.5 to return only certain detections
DECL|field|DOCID_THRESHOLD_DEFAULT
name|Double
name|DOCID_THRESHOLD_DEFAULT
init|=
literal|0.5
decl_stmt|;
block|}
end_interface

end_unit

