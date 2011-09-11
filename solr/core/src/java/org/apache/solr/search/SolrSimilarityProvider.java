begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
operator|.
name|DefaultSimilarityProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
operator|.
name|Similarity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|FieldType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
import|;
end_import

begin_comment
comment|/**  * SimilarityProvider that uses the default Lucene implementation, unless  * otherwise specified by the fieldtype.  *<p>  * You can extend this class to customize the behavior of the parts  * of lucene's ranking system that are not field-specific, such as  * {@link #coord(int, int)} and {@link #queryNorm(float)}.  */
end_comment

begin_class
DECL|class|SolrSimilarityProvider
specifier|public
class|class
name|SolrSimilarityProvider
extends|extends
name|DefaultSimilarityProvider
block|{
DECL|field|schema
specifier|private
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|method|SolrSimilarityProvider
specifier|public
name|SolrSimilarityProvider
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
block|}
comment|/**     * Solr implementation delegates to the fieldtype's similarity.    * If this does not exist, uses the schema's default similarity.    */
comment|// note: this is intentionally final, to maintain consistency with
comment|// whatever is specified in the the schema!
annotation|@
name|Override
DECL|method|get
specifier|public
specifier|final
name|Similarity
name|get
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|FieldType
name|fieldType
init|=
name|schema
operator|.
name|getFieldTypeNoEx
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
return|return
name|schema
operator|.
name|getFallbackSimilarity
argument_list|()
return|;
block|}
else|else
block|{
name|Similarity
name|similarity
init|=
name|fieldType
operator|.
name|getSimilarity
argument_list|()
decl_stmt|;
return|return
name|similarity
operator|==
literal|null
condition|?
name|schema
operator|.
name|getFallbackSimilarity
argument_list|()
else|:
name|similarity
return|;
block|}
block|}
block|}
end_class

end_unit

