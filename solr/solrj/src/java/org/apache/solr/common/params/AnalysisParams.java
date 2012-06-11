begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_comment
comment|/**  * Defines the request parameters used by all analysis request handlers.  *  *  * @since solr 1.4  */
end_comment

begin_interface
DECL|interface|AnalysisParams
specifier|public
interface|interface
name|AnalysisParams
block|{
comment|/**    * The prefix for all parameters.    */
DECL|field|PREFIX
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"analysis"
decl_stmt|;
comment|/**    * Holds the query to be analyzed.    */
DECL|field|QUERY
specifier|static
specifier|final
name|String
name|QUERY
init|=
name|PREFIX
operator|+
literal|".query"
decl_stmt|;
comment|/**    * Set to {@code true} to indicate that the index tokens that match query tokens should be marked as "mateched".    */
DECL|field|SHOW_MATCH
specifier|static
specifier|final
name|String
name|SHOW_MATCH
init|=
name|PREFIX
operator|+
literal|".showmatch"
decl_stmt|;
comment|//===================================== FieldAnalysisRequestHandler Params =========================================
comment|/**    * Holds the value of the field which should be analyzed.    */
DECL|field|FIELD_NAME
specifier|static
specifier|final
name|String
name|FIELD_NAME
init|=
name|PREFIX
operator|+
literal|".fieldname"
decl_stmt|;
comment|/**    * Holds a comma-separated list of field types that the analysis should be peformed for.    */
DECL|field|FIELD_TYPE
specifier|static
specifier|final
name|String
name|FIELD_TYPE
init|=
name|PREFIX
operator|+
literal|".fieldtype"
decl_stmt|;
comment|/**    * Hodls a comma-separated list of field named that the analysis should be performed for.    */
DECL|field|FIELD_VALUE
specifier|static
specifier|final
name|String
name|FIELD_VALUE
init|=
name|PREFIX
operator|+
literal|".fieldvalue"
decl_stmt|;
block|}
end_interface

end_unit

