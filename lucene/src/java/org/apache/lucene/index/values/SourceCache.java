begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Comparator
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
name|values
operator|.
name|DocValues
operator|.
name|SortedSource
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
name|values
operator|.
name|DocValues
operator|.
name|Source
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

begin_comment
comment|/**  * Abstract base class for {@link DocValues} {@link Source} /  * {@link SortedSource} cache.  *<p>  * {@link Source} and {@link SortedSource} instances loaded via  * {@link DocValues#load()} and {@link DocValues#loadSorted(Comparator)} are  * entirely memory resident and need to be maintained by the caller. Each call  * to {@link DocValues#load()} or {@link DocValues#loadSorted(Comparator)} will  * cause an entire reload of the underlying data. Source and  * {@link SortedSource} instances obtained from {@link DocValues#getSource()}  * and {@link DocValues#getSource()} respectively are maintained by a  * {@link SourceCache} that is closed ({@link #close(DocValues)}) once the  * {@link IndexReader} that created the {@link DocValues} instance is closed.  *<p>  * Unless {@link Source} and {@link SortedSource} instances are managed by  * another entity it is recommended to use the cached variants to obtain a  * source instance.  *<p>  * Implementation of this API must be thread-safe.  *   * @see DocValues#setCache(SourceCache)  * @see DocValues#getSource()  * @see DocValues#getSortedSorted(Comparator)  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|SourceCache
specifier|public
specifier|abstract
class|class
name|SourceCache
block|{
comment|/**    * Atomically loads a {@link Source} into the cache from the given    * {@link DocValues} and returns it iff no other {@link Source} has already    * been cached. Otherwise the cached source is returned.    *<p>    * This method will not return<code>null</code>    */
DECL|method|load
specifier|public
specifier|abstract
name|Source
name|load
parameter_list|(
name|DocValues
name|values
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Atomically loads a {@link SortedSource} into the cache from the given    * {@link DocValues} and returns it iff no other {@link SortedSource} has    * already been cached. Otherwise the cached source is returned.    *<p>    * This method will not return<code>null</code>    */
DECL|method|loadSorted
specifier|public
specifier|abstract
name|SortedSource
name|loadSorted
parameter_list|(
name|DocValues
name|values
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Atomically invalidates the cached {@link Source} and {@link SortedSource}    * instances if any and empties the cache.    */
DECL|method|invalidate
specifier|public
specifier|abstract
name|void
name|invalidate
parameter_list|(
name|DocValues
name|values
parameter_list|)
function_decl|;
comment|/**    * Atomically closes the cache and frees all resources.    */
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|(
name|DocValues
name|values
parameter_list|)
block|{
name|invalidate
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
comment|/**    * Simple per {@link DocValues} instance cache implementation that holds a    * {@link Source} and {@link SortedSource} reference as a member variable.    *<p>    * If a {@link DirectSourceCache} instance is closed or invalidated the cached    * reference are simply set to<code>null</code>    */
DECL|class|DirectSourceCache
specifier|public
specifier|static
specifier|final
class|class
name|DirectSourceCache
extends|extends
name|SourceCache
block|{
DECL|field|ref
specifier|private
name|Source
name|ref
decl_stmt|;
DECL|field|sortedRef
specifier|private
name|SortedSource
name|sortedRef
decl_stmt|;
DECL|method|load
specifier|public
specifier|synchronized
name|Source
name|load
parameter_list|(
name|DocValues
name|values
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|ref
operator|==
literal|null
condition|)
block|{
name|ref
operator|=
name|values
operator|.
name|load
argument_list|()
expr_stmt|;
block|}
return|return
name|ref
return|;
block|}
DECL|method|loadSorted
specifier|public
specifier|synchronized
name|SortedSource
name|loadSorted
parameter_list|(
name|DocValues
name|values
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|sortedRef
operator|==
literal|null
condition|)
block|{
name|sortedRef
operator|=
name|values
operator|.
name|loadSorted
argument_list|(
name|comp
argument_list|)
expr_stmt|;
block|}
return|return
name|sortedRef
return|;
block|}
DECL|method|invalidate
specifier|public
specifier|synchronized
name|void
name|invalidate
parameter_list|(
name|DocValues
name|values
parameter_list|)
block|{
name|ref
operator|=
literal|null
expr_stmt|;
name|sortedRef
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

