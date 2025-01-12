begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
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
name|SolrDocument
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
DECL|class|MockSolrEntityProcessor
specifier|public
class|class
name|MockSolrEntityProcessor
extends|extends
name|SolrEntityProcessor
block|{
DECL|field|docsData
specifier|private
specifier|final
name|List
argument_list|<
name|SolrTestCaseJ4
operator|.
name|Doc
argument_list|>
name|docsData
decl_stmt|;
comment|//  private final int rows;
DECL|field|queryCount
specifier|private
name|int
name|queryCount
init|=
literal|0
decl_stmt|;
DECL|field|rows
specifier|private
name|int
name|rows
decl_stmt|;
DECL|field|start
specifier|private
name|int
name|start
init|=
literal|0
decl_stmt|;
DECL|method|MockSolrEntityProcessor
specifier|public
name|MockSolrEntityProcessor
parameter_list|(
name|List
argument_list|<
name|SolrTestCaseJ4
operator|.
name|Doc
argument_list|>
name|docsData
parameter_list|,
name|int
name|rows
parameter_list|)
block|{
name|this
operator|.
name|docsData
operator|=
name|docsData
expr_stmt|;
name|this
operator|.
name|rows
operator|=
name|rows
expr_stmt|;
block|}
comment|//@Override
comment|//protected SolrDocumentList doQuery(int start) {
comment|//  queryCount++;
comment|//  return getDocs(start, rows);
comment|// }
annotation|@
name|Override
DECL|method|buildIterator
specifier|protected
name|void
name|buildIterator
parameter_list|()
block|{
if|if
condition|(
name|rowIterator
operator|==
literal|null
operator|||
operator|(
operator|!
name|rowIterator
operator|.
name|hasNext
argument_list|()
operator|&&
operator|(
operator|(
name|SolrDocumentListIterator
operator|)
name|rowIterator
operator|)
operator|.
name|hasMoreRows
argument_list|()
operator|)
condition|)
block|{
name|queryCount
operator|++
expr_stmt|;
name|SolrDocumentList
name|docs
init|=
name|getDocs
argument_list|(
name|start
argument_list|,
name|rows
argument_list|)
decl_stmt|;
name|rowIterator
operator|=
operator|new
name|SolrDocumentListIterator
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|start
operator|+=
name|docs
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getDocs
specifier|private
name|SolrDocumentList
name|getDocs
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|rows
parameter_list|)
block|{
name|SolrDocumentList
name|docs
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|docs
operator|.
name|setNumFound
argument_list|(
name|docsData
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|docs
operator|.
name|setStart
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|int
name|endIndex
init|=
name|start
operator|+
name|rows
decl_stmt|;
name|int
name|end
init|=
name|docsData
operator|.
name|size
argument_list|()
operator|<
name|endIndex
condition|?
name|docsData
operator|.
name|size
argument_list|()
else|:
name|endIndex
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|SolrDocument
name|doc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|SolrTestCaseJ4
operator|.
name|Doc
name|testDoc
init|=
name|docsData
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|testDoc
operator|.
name|id
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"description"
argument_list|,
name|testDoc
operator|.
name|getValues
argument_list|(
literal|"description"
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|docs
return|;
block|}
DECL|method|getQueryCount
specifier|public
name|int
name|getQueryCount
parameter_list|()
block|{
return|return
name|queryCount
return|;
block|}
block|}
end_class

end_unit

