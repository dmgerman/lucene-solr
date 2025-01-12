begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.grouping.endresulttransformer
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|grouping
operator|.
name|endresulttransformer
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
name|search
operator|.
name|ScoreDoc
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
name|grouping
operator|.
name|GroupDocs
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
name|grouping
operator|.
name|TopGroups
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
name|util
operator|.
name|BytesRef
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
name|common
operator|.
name|SolrDocumentList
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
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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
name|SchemaField
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
name|search
operator|.
name|SolrIndexSearcher
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
name|search
operator|.
name|grouping
operator|.
name|distributed
operator|.
name|command
operator|.
name|QueryCommandResult
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link EndResultTransformer} that keeps each grouped result separate in the final response.  */
end_comment

begin_class
DECL|class|GroupedEndResultTransformer
specifier|public
class|class
name|GroupedEndResultTransformer
implements|implements
name|EndResultTransformer
block|{
DECL|field|searcher
specifier|private
specifier|final
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|method|GroupedEndResultTransformer
specifier|public
name|GroupedEndResultTransformer
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|transform
specifier|public
name|void
name|transform
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|result
parameter_list|,
name|ResponseBuilder
name|rb
parameter_list|,
name|SolrDocumentSource
name|solrDocumentSource
parameter_list|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|commands
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|entry
range|:
name|result
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|TopGroups
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|value
argument_list|)
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
name|topGroups
init|=
operator|(
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
operator|)
name|value
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|command
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|command
operator|.
name|add
argument_list|(
literal|"matches"
argument_list|,
name|rb
operator|.
name|totalHitCount
argument_list|)
expr_stmt|;
name|Integer
name|totalGroupCount
init|=
name|rb
operator|.
name|mergedGroupCounts
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|totalGroupCount
operator|!=
literal|null
condition|)
block|{
name|command
operator|.
name|add
argument_list|(
literal|"ngroups"
argument_list|,
name|totalGroupCount
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|NamedList
argument_list|>
name|groups
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|SchemaField
name|groupField
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|FieldType
name|groupFieldType
init|=
name|groupField
operator|.
name|getType
argument_list|()
decl_stmt|;
for|for
control|(
name|GroupDocs
argument_list|<
name|BytesRef
argument_list|>
name|group
range|:
name|topGroups
operator|.
name|groups
control|)
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|groupResult
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|group
operator|.
name|groupValue
operator|!=
literal|null
condition|)
block|{
name|groupResult
operator|.
name|add
argument_list|(
literal|"groupValue"
argument_list|,
name|groupFieldType
operator|.
name|toObject
argument_list|(
name|groupField
operator|.
name|createField
argument_list|(
name|group
operator|.
name|groupValue
operator|.
name|utf8ToString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|groupResult
operator|.
name|add
argument_list|(
literal|"groupValue"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|SolrDocumentList
name|docList
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|docList
operator|.
name|setNumFound
argument_list|(
name|group
operator|.
name|totalHits
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|group
operator|.
name|maxScore
argument_list|)
condition|)
block|{
name|docList
operator|.
name|setMaxScore
argument_list|(
name|group
operator|.
name|maxScore
argument_list|)
expr_stmt|;
block|}
name|docList
operator|.
name|setStart
argument_list|(
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getWithinGroupOffset
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|group
operator|.
name|scoreDocs
control|)
block|{
name|docList
operator|.
name|add
argument_list|(
name|solrDocumentSource
operator|.
name|retrieve
argument_list|(
name|scoreDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|groupResult
operator|.
name|add
argument_list|(
literal|"doclist"
argument_list|,
name|docList
argument_list|)
expr_stmt|;
name|groups
operator|.
name|add
argument_list|(
name|groupResult
argument_list|)
expr_stmt|;
block|}
name|command
operator|.
name|add
argument_list|(
literal|"groups"
argument_list|,
name|groups
argument_list|)
expr_stmt|;
name|commands
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|command
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|QueryCommandResult
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|QueryCommandResult
name|queryCommandResult
init|=
operator|(
name|QueryCommandResult
operator|)
name|value
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|command
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|command
operator|.
name|add
argument_list|(
literal|"matches"
argument_list|,
name|queryCommandResult
operator|.
name|getMatches
argument_list|()
argument_list|)
expr_stmt|;
name|SolrDocumentList
name|docList
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|docList
operator|.
name|setNumFound
argument_list|(
name|queryCommandResult
operator|.
name|getTopDocs
argument_list|()
operator|.
name|totalHits
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|queryCommandResult
operator|.
name|getTopDocs
argument_list|()
operator|.
name|getMaxScore
argument_list|()
argument_list|)
condition|)
block|{
name|docList
operator|.
name|setMaxScore
argument_list|(
name|queryCommandResult
operator|.
name|getTopDocs
argument_list|()
operator|.
name|getMaxScore
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|docList
operator|.
name|setStart
argument_list|(
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getWithinGroupOffset
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|queryCommandResult
operator|.
name|getTopDocs
argument_list|()
operator|.
name|scoreDocs
control|)
block|{
name|docList
operator|.
name|add
argument_list|(
name|solrDocumentSource
operator|.
name|retrieve
argument_list|(
name|scoreDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|command
operator|.
name|add
argument_list|(
literal|"doclist"
argument_list|,
name|docList
argument_list|)
expr_stmt|;
name|commands
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|command
argument_list|)
expr_stmt|;
block|}
block|}
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"grouped"
argument_list|,
name|commands
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

