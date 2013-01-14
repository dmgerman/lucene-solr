begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search.aggregator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
operator|.
name|aggregator
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|AtomicReaderContext
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
name|IntsRef
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Aggregates the categories of documents given to  * {@link #aggregate(int, float, IntsRef)}. Note that the document IDs are local  * to the reader given to {@link #setNextReader(AtomicReaderContext)}.  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|Aggregator
specifier|public
interface|interface
name|Aggregator
block|{
comment|/**    * Sets the {@link AtomicReaderContext} for which    * {@link #aggregate(int, float, IntsRef)} calls will be made. If this method    * returns false, {@link #aggregate(int, float, IntsRef)} should not be called    * for this reader.    */
DECL|method|setNextReader
specifier|public
name|boolean
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Aggregate the ordinals of the given document ID (and its score). The given    * ordinals offset is always zero.    */
DECL|method|aggregate
specifier|public
name|void
name|aggregate
parameter_list|(
name|int
name|docID
parameter_list|,
name|float
name|score
parameter_list|,
name|IntsRef
name|ordinals
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

