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
comment|/**  * A collection of params used in DisMaxRequestHandler,  * both for Plugin initialization and for Requests.  */
end_comment

begin_interface
DECL|interface|DisMaxParams
specifier|public
interface|interface
name|DisMaxParams
block|{
comment|/** query and init param for tiebreaker value */
DECL|field|TIE
specifier|public
specifier|static
name|String
name|TIE
init|=
literal|"tie"
decl_stmt|;
comment|/** query and init param for query fields */
DECL|field|QF
specifier|public
specifier|static
name|String
name|QF
init|=
literal|"qf"
decl_stmt|;
comment|/** query and init param for phrase boost fields */
DECL|field|PF
specifier|public
specifier|static
name|String
name|PF
init|=
literal|"pf"
decl_stmt|;
comment|/** query and init param for MinShouldMatch specification */
DECL|field|MM
specifier|public
specifier|static
name|String
name|MM
init|=
literal|"mm"
decl_stmt|;
comment|/**    * query and init param for Phrase Slop value in phrase    * boost query (in pf fields)    */
DECL|field|PS
specifier|public
specifier|static
name|String
name|PS
init|=
literal|"ps"
decl_stmt|;
comment|/**    * query and init param for phrase Slop value in phrases    * explicitly included in the user's query string ( in qf fields)    */
DECL|field|QS
specifier|public
specifier|static
name|String
name|QS
init|=
literal|"qs"
decl_stmt|;
comment|/** query and init param for boosting query */
DECL|field|BQ
specifier|public
specifier|static
name|String
name|BQ
init|=
literal|"bq"
decl_stmt|;
comment|/** query and init param for boosting functions */
DECL|field|BF
specifier|public
specifier|static
name|String
name|BF
init|=
literal|"bf"
decl_stmt|;
comment|/**    * Alternate query (expressed in Solr QuerySyntax)    * to use if main query (q) is empty    */
DECL|field|ALTQ
specifier|public
specifier|static
name|String
name|ALTQ
init|=
literal|"q.alt"
decl_stmt|;
comment|/** query and init param for field list */
DECL|field|GEN
specifier|public
specifier|static
name|String
name|GEN
init|=
literal|"gen"
decl_stmt|;
block|}
end_interface

end_unit

