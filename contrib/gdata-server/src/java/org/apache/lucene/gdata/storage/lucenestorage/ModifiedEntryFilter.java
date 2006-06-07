begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.storage.lucenestorage
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
operator|.
name|lucenestorage
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|Term
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
name|index
operator|.
name|TermDocs
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
name|Filter
import|;
end_import

begin_comment
comment|/**   * The {@link ModifiedEntryFilter} filters the given entryIds from the lucene   * {@link org.apache.lucene.search.Hits} set. This filter is used to prevent the   * storage from retrieving already deleted or updated entries still remainig in   * the {@link org.apache.lucene.gdata.storage.lucenestorage.StorageBuffer}.   *    * @see org.apache.lucene.search.Filter   *    * @author Simon Willnauer   *    */
end_comment

begin_class
DECL|class|ModifiedEntryFilter
specifier|public
class|class
name|ModifiedEntryFilter
extends|extends
name|Filter
block|{
comment|/**       * impl Serializable       */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|1551686287704213591L
decl_stmt|;
DECL|field|entyIds
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|entyIds
decl_stmt|;
comment|/**       * Creates a new {@link ModifiedEntryFilter}       * @param entryIds the entry id's to filter        *        */
DECL|method|ModifiedEntryFilter
specifier|public
name|ModifiedEntryFilter
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|entryIds
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|entyIds
operator|=
name|entryIds
expr_stmt|;
block|}
comment|/**       * @see org.apache.lucene.search.Filter#bits(org.apache.lucene.index.IndexReader)       */
annotation|@
name|Override
DECL|method|bits
specifier|public
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|BitSet
name|bitSet
init|=
operator|new
name|BitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|bitSet
operator|.
name|flip
argument_list|(
literal|0
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// set all docs
name|int
index|[]
name|docs
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
name|int
index|[]
name|freq
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|this
operator|.
name|entyIds
control|)
block|{
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|(
operator|new
name|Term
argument_list|(
name|StorageEntryWrapper
operator|.
name|FIELD_ENTRY_ID
argument_list|,
name|id
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|count
init|=
name|termDocs
operator|.
name|read
argument_list|(
name|docs
argument_list|,
name|freq
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|1
condition|)
name|bitSet
operator|.
name|flip
argument_list|(
name|docs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|bitSet
return|;
block|}
block|}
end_class

end_unit

