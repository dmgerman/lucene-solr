begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.spelling.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|CharsRef
import|;
end_import

begin_comment
comment|/**  * Encapsulates the inputs required to be passed on to   * the underlying suggester in {@link SolrSuggester}  **/
end_comment

begin_class
DECL|class|SuggesterOptions
specifier|public
class|class
name|SuggesterOptions
block|{
comment|/** The token to lookup */
DECL|field|token
name|CharsRef
name|token
decl_stmt|;
comment|/** Number of suggestions requested */
DECL|field|count
name|int
name|count
decl_stmt|;
comment|/** A Solr or Lucene query for filtering suggestions*/
DECL|field|contextFilterQuery
name|String
name|contextFilterQuery
decl_stmt|;
comment|/** Are all terms required?*/
DECL|field|allTermsRequired
name|boolean
name|allTermsRequired
decl_stmt|;
comment|/** Highlight term in results?*/
DECL|field|highlight
name|boolean
name|highlight
decl_stmt|;
DECL|method|SuggesterOptions
specifier|public
name|SuggesterOptions
parameter_list|(
name|CharsRef
name|token
parameter_list|,
name|int
name|count
parameter_list|,
name|String
name|contextFilterQuery
parameter_list|,
name|boolean
name|allTermsRequired
parameter_list|,
name|boolean
name|highlight
parameter_list|)
block|{
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|contextFilterQuery
operator|=
name|contextFilterQuery
expr_stmt|;
name|this
operator|.
name|allTermsRequired
operator|=
name|allTermsRequired
expr_stmt|;
name|this
operator|.
name|highlight
operator|=
name|highlight
expr_stmt|;
block|}
block|}
end_class

end_unit

