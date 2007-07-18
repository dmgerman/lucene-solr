begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Copyright 2007 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/** Abstract base class providing a mechanism to restrict searches to a subset  of an index and also maintains and returns position information.   This is useful if you want to compare the positions from a SpanQuery with the positions of items in  a filter.  For instance, if you had a SpanFilter that marked all the occurrences of the word "foo" in documents,  and then you entered a new SpanQuery containing bar, you could not only filter by the word foo, but you could  then compare position information for post processing.  */
end_comment

begin_class
DECL|class|SpanFilter
specifier|public
specifier|abstract
class|class
name|SpanFilter
extends|extends
name|Filter
block|{
comment|/** Returns a SpanFilterResult with true for documents which should be permitted in     search results, and false for those that should not and Spans for where the true docs match.    * @param reader The {@link org.apache.lucene.index.IndexReader} to load position and bitset information from    * @return A {@link SpanFilterResult}    * @throws java.io.IOException if there was an issue accessing the necessary information    * */
DECL|method|bitSpans
specifier|public
specifier|abstract
name|SpanFilterResult
name|bitSpans
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

