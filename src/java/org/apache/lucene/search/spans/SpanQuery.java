begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
name|Collection
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
name|search
operator|.
name|Query
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
name|Weight
import|;
end_import

begin_comment
comment|/** Base class for span-based queries. */
end_comment

begin_class
DECL|class|SpanQuery
specifier|public
specifier|abstract
class|class
name|SpanQuery
extends|extends
name|Query
block|{
comment|/** Expert: Returns the matches for this query in an index.  Used internally    * to search for spans. */
DECL|method|getSpans
specifier|public
specifier|abstract
name|Spans
name|getSpans
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns the name of the field matched by this query.*/
DECL|method|getField
specifier|public
specifier|abstract
name|String
name|getField
parameter_list|()
function_decl|;
comment|/** Returns a collection of all terms matched by this query.    * @deprecated use extractTerms instead    * @see Query#extractTerms(Set)    */
DECL|method|getTerms
specifier|public
specifier|abstract
name|Collection
name|getTerms
parameter_list|()
function_decl|;
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SpanWeight
argument_list|(
name|this
argument_list|,
name|searcher
argument_list|)
return|;
block|}
block|}
end_class

end_unit

