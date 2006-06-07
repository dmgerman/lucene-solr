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
name|io
operator|.
name|StringReader
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
name|Iterator
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
name|document
operator|.
name|Document
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
name|gdata
operator|.
name|server
operator|.
name|FeedNotFoundException
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
name|gdata
operator|.
name|server
operator|.
name|GDataEntityBuilder
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
name|search
operator|.
name|BooleanClause
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
name|BooleanQuery
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
name|Hit
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
name|Hits
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
name|Searcher
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
name|Sort
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
name|SortField
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
name|TermQuery
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
name|BooleanClause
operator|.
name|Occur
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseEntry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|ExtensionProfile
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|util
operator|.
name|ParseException
import|;
end_import

begin_comment
comment|/**   * StorageQuery wrapps a Lucene {@link org.apache.lucene.search.IndexSearcher}   * and a {@link org.apache.lucene.gdata.storage.lucenestorage.StorageBuffer} to   * perform all request on the lucene storage.   * The wrapped components are thread - safe.   *<p>   * An instance of this class will serve all client requests. To obtain the   * current instance of the {@link StorageQuery} the method   * {@link org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController#getStorageQuery()}   * has to be invoked. This method will release the current StorageQuery.   *</p>   * @see org.apache.lucene.search.IndexSearcher   * @see org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController   * @see org.apache.lucene.gdata.storage.lucenestorage.StorageBuffer   *    * @author Simon Willnauer   *    */
end_comment

begin_class
DECL|class|StorageQuery
specifier|public
class|class
name|StorageQuery
block|{
DECL|field|buffer
specifier|private
specifier|final
name|StorageBuffer
name|buffer
decl_stmt|;
DECL|field|searcher
specifier|private
specifier|final
name|Searcher
name|searcher
decl_stmt|;
comment|/*       * Sort the result by timestamp desc       */
DECL|field|timeStampSort
specifier|private
specifier|final
name|Sort
name|timeStampSort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
name|StorageEntryWrapper
operator|.
name|FIELD_TIMESTAMP
argument_list|,
name|SortField
operator|.
name|STRING
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
comment|/**       * Creates a new StorageQuery       *        * @param buffer -       *            the buffer instance to get the buffered inserts, updates from.       * @param searcher -       *            the searcher instance to use to query the storage index.       *        *        */
DECL|method|StorageQuery
specifier|protected
name|StorageQuery
parameter_list|(
specifier|final
name|StorageBuffer
name|buffer
parameter_list|,
specifier|final
name|Searcher
name|searcher
parameter_list|)
block|{
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
block|}
DECL|method|storageQuery
specifier|private
name|Hits
name|storageQuery
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|entryId
parameter_list|)
throws|throws
name|IOException
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
comment|/*           * query the index using a BooleanQuery           */
for|for
control|(
name|String
name|id
range|:
name|entryId
control|)
block|{
name|TermQuery
name|termQuery
init|=
operator|new
name|TermQuery
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
comment|// use an OR query
name|query
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|termQuery
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|this
operator|.
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
operator|new
name|ModifiedEntryFilter
argument_list|(
name|this
operator|.
name|buffer
operator|.
name|getExculdList
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/*       * query the storage index for a entire feed.       */
DECL|method|storageFeedQuery
specifier|private
name|Hits
name|storageFeedQuery
parameter_list|(
specifier|final
name|String
name|feedId
parameter_list|,
specifier|final
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
name|TermQuery
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|StorageEntryWrapper
operator|.
name|FIELD_FEED_ID
argument_list|,
name|feedId
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|this
operator|.
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
operator|new
name|ModifiedEntryFilter
argument_list|(
name|this
operator|.
name|buffer
operator|.
name|getExculdList
argument_list|()
argument_list|)
argument_list|,
name|sort
argument_list|)
return|;
block|}
comment|/*       * get a single entry       */
DECL|method|storageQuery
specifier|private
name|Hits
name|storageQuery
parameter_list|(
name|String
name|entryId
parameter_list|)
throws|throws
name|IOException
block|{
name|TermQuery
name|termQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|StorageEntryWrapper
operator|.
name|FIELD_ENTRY_ID
argument_list|,
name|entryId
argument_list|)
argument_list|)
decl_stmt|;
comment|/*           * Filter entries inside the buffer, buffered entries might contain           * deleted entries. These entries must be found!!           */
return|return
name|this
operator|.
name|searcher
operator|.
name|search
argument_list|(
name|termQuery
argument_list|,
operator|new
name|ModifiedEntryFilter
argument_list|(
name|this
operator|.
name|buffer
operator|.
name|getExculdList
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**       * This method fetches the latest feed entries from the storage. Feed       * ususaly requested via a search query or as a simple query to the REST       * interface.       *<p>       * The REST interface requestes all the entries from a Storage. The Storage       * retrieves the entries corresponding to the parameters specified. This       * method first requests the latest entries or updated entries from the       * {@link StorageBuffer}. If the buffer already contains enought entries       * for the the specified result count the entires will be returned. If not,       * the underlaying lucene index will be searcher for all documents of the       * specified feed sorted by storing timestamp desc.       *</p>       *<p>       * The entries will be searched in a feed context specified by the given       * feed ID       *</p>       *        *        * @param feedId -       *            the requested feed, this id will be used to retrieve the       *            entries.       * @param resultCount -       *            how many entries are requested       * @param startIndex -       *            the offset of the entriy to start from.       * @param profil -       *            the extension profile used to create the entriy instances       * @return - an ordered list of {@link BaseEntry} objects, or an empty list       *         if no entries could be found       * @throws IOException -       *             if the index could not be queries or the entries could not be       *             build       * @throws FeedNotFoundException -       *             if the requested feed is not registered       * @throws ParseException -       *             if an entry could not be parsed while building it from the       *             Lucene Document.       */
comment|// TODO check input parameter
DECL|method|getLatestFeedQuery
specifier|public
name|List
argument_list|<
name|BaseEntry
argument_list|>
name|getLatestFeedQuery
parameter_list|(
specifier|final
name|String
name|feedId
parameter_list|,
specifier|final
name|int
name|resultCount
parameter_list|,
specifier|final
name|int
name|startIndex
parameter_list|,
specifier|final
name|ExtensionProfile
name|profil
parameter_list|)
throws|throws
name|IOException
throws|,
name|FeedNotFoundException
throws|,
name|ParseException
block|{
name|List
argument_list|<
name|BaseEntry
argument_list|>
name|returnList
init|=
operator|new
name|ArrayList
argument_list|<
name|BaseEntry
argument_list|>
argument_list|(
name|resultCount
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StorageEntryWrapper
argument_list|>
name|bufferedWrapperList
init|=
name|this
operator|.
name|buffer
operator|.
name|getSortedEntries
argument_list|(
name|feedId
argument_list|)
decl_stmt|;
name|int
name|alreadyAdded
init|=
literal|0
decl_stmt|;
name|int
name|offset
init|=
name|startIndex
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|bufferedWrapperList
operator|!=
literal|null
operator|&&
name|bufferedWrapperList
operator|.
name|size
argument_list|()
operator|>=
name|startIndex
condition|)
block|{
for|for
control|(
init|;
name|alreadyAdded
operator|<
name|resultCount
condition|;
name|alreadyAdded
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|bufferedWrapperList
operator|.
name|size
argument_list|()
operator|-
name|offset
operator|)
operator|>
literal|0
condition|)
block|{
name|StorageEntryWrapper
name|wrappedEntry
init|=
name|bufferedWrapperList
operator|.
name|get
argument_list|(
name|offset
operator|++
argument_list|)
decl_stmt|;
name|returnList
operator|.
name|add
argument_list|(
name|wrappedEntry
operator|.
name|getEntry
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
break|break;
block|}
comment|// reset offset
name|offset
operator|=
name|startIndex
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|alreadyAdded
operator|==
name|resultCount
condition|)
return|return
name|returnList
return|;
block|}
else|else
block|{
comment|/*               * if the buffersize is less than the startindex the buffersize must               * be considered. Sublists would not be a repeatable read part of               * the whole list               */
if|if
condition|(
name|bufferedWrapperList
operator|!=
literal|null
condition|)
name|offset
operator|=
name|startIndex
operator|-
literal|1
operator|-
name|bufferedWrapperList
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|Hits
name|hits
init|=
name|storageFeedQuery
argument_list|(
name|feedId
argument_list|,
name|this
operator|.
name|timeStampSort
argument_list|)
decl_stmt|;
if|if
condition|(
name|hits
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
init|;
operator|(
name|offset
operator|<
name|hits
operator|.
name|length
argument_list|()
operator|)
operator|&&
operator|(
name|alreadyAdded
operator|<
name|resultCount
operator|)
condition|;
name|offset
operator|++
operator|,
name|alreadyAdded
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|hits
operator|.
name|doc
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|BaseEntry
name|entry
init|=
name|buildEntryFromLuceneDocument
argument_list|(
name|doc
argument_list|,
name|profil
argument_list|)
decl_stmt|;
name|returnList
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|returnList
return|;
block|}
comment|/**       * This method retrieves a single entry from the storage. If the       * {@link StorageBuffer} does not contain the requested entry the       * underlaying storage index will be searched.       *<p>       * The Entry will be searched in a feed context specified by the given feed       * ID       *</p>       *        * @param entryId -       *            the entry to fetch       * @param feedId -       *            the feedid eg. feed context       * @param profil -       *            the extension profile used to create the entriy instances       * @return - the requested {@link BaseEntry} or<code>null</code> if the       *         entry can not be found       * @throws IOException -       *             if the index could not be queries or the entries could not be       *             build       * @throws FeedNotFoundException -       *             if the requested feed is not registered       * @throws ParseException -       *             if an entry could not be parsed while building it from the       *             Lucene Document.       */
DECL|method|singleEntryQuery
specifier|public
name|BaseEntry
name|singleEntryQuery
parameter_list|(
specifier|final
name|String
name|entryId
parameter_list|,
specifier|final
name|String
name|feedId
parameter_list|,
specifier|final
name|ExtensionProfile
name|profil
parameter_list|)
throws|throws
name|IOException
throws|,
name|FeedNotFoundException
throws|,
name|ParseException
block|{
name|StorageEntryWrapper
name|wrapper
init|=
name|this
operator|.
name|buffer
operator|.
name|getEntry
argument_list|(
name|entryId
argument_list|,
name|feedId
argument_list|)
decl_stmt|;
if|if
condition|(
name|wrapper
operator|==
literal|null
condition|)
block|{
name|Hits
name|hits
init|=
name|storageQuery
argument_list|(
name|entryId
argument_list|)
decl_stmt|;
if|if
condition|(
name|hits
operator|.
name|length
argument_list|()
operator|<=
literal|0
condition|)
return|return
literal|null
return|;
name|Document
name|doc
init|=
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|buildEntryFromLuceneDocument
argument_list|(
name|doc
argument_list|,
name|profil
argument_list|)
return|;
block|}
return|return
name|wrapper
operator|.
name|getEntry
argument_list|()
return|;
block|}
comment|/**       * Fetches the requested entries from the storage. The given list contains       * entry ids to be looked up in the storage. First the {@link StorageBuffer}       * will be queried for the entry ids. If not all of the entries remain in       * the buffer the underlaying lucene index will be searched. The entries are       * not guaranteed to be in the same order as they are in the given id list.       * Entry ID's not found in the index or the buffer will be omitted.       *<p>       * The entries will be searched in a feed context specified by the given       * feed ID       *</p>       *        * @param entryIds -       *            the entriy ids to fetch.       * @param feedId -       *            the feed id eg. feed context.       * @param profil -       *            the extension profile used to create the entry instances.       * @return - the list of entries corresponding to the given entry id list.       * @throws IOException -       *             if the index could not be queries or the entries could not be       *             build       * @throws FeedNotFoundException -       *             if the requested feed is not registered       * @throws ParseException -       *             if an entry could not be parsed while building it from the       *             Lucene Document.       */
DECL|method|entryQuery
specifier|public
name|List
argument_list|<
name|BaseEntry
argument_list|>
name|entryQuery
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|entryIds
parameter_list|,
specifier|final
name|String
name|feedId
parameter_list|,
specifier|final
name|ExtensionProfile
name|profil
parameter_list|)
throws|throws
name|IOException
throws|,
name|FeedNotFoundException
throws|,
name|ParseException
block|{
name|List
argument_list|<
name|BaseEntry
argument_list|>
name|resultList
init|=
operator|new
name|ArrayList
argument_list|<
name|BaseEntry
argument_list|>
argument_list|(
name|entryIds
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|searchList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|entryIds
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|entry
range|:
name|entryIds
control|)
block|{
name|StorageEntryWrapper
name|bufferedEntry
init|=
name|this
operator|.
name|buffer
operator|.
name|getEntry
argument_list|(
name|entry
argument_list|,
name|feedId
argument_list|)
decl_stmt|;
if|if
condition|(
name|bufferedEntry
operator|!=
literal|null
condition|)
block|{
name|resultList
operator|.
name|add
argument_list|(
name|bufferedEntry
operator|.
name|getEntry
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
name|searchList
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|searchList
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|resultList
return|;
name|Hits
name|hits
init|=
name|storageQuery
argument_list|(
name|searchList
argument_list|)
decl_stmt|;
name|Iterator
name|hitIterator
init|=
name|hits
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|hitIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Hit
name|hit
init|=
operator|(
name|Hit
operator|)
name|hitIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|hit
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|BaseEntry
name|entry
init|=
name|buildEntryFromLuceneDocument
argument_list|(
name|doc
argument_list|,
name|profil
argument_list|)
decl_stmt|;
name|resultList
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
return|return
name|resultList
return|;
block|}
DECL|method|buildEntryFromLuceneDocument
specifier|private
name|BaseEntry
name|buildEntryFromLuceneDocument
parameter_list|(
specifier|final
name|Document
name|doc
parameter_list|,
specifier|final
name|ExtensionProfile
name|profil
parameter_list|)
throws|throws
name|FeedNotFoundException
throws|,
name|ParseException
throws|,
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|StorageEntryWrapper
operator|.
name|FIELD_CONTENT
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|GDataEntityBuilder
operator|.
name|buildEntry
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|StorageEntryWrapper
operator|.
name|FIELD_FEED_ID
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|,
name|reader
argument_list|,
name|profil
argument_list|)
return|;
block|}
comment|/**       * Closes all resources used in the {@link StorageQuery}. The instance can       * not be reused after invoking this method.       *        * @throws IOException -       *             if the resouces can not be closed       */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|buffer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

