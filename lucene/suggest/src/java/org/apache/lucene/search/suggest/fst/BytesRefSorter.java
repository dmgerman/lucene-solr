begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|fst
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
name|lucene
operator|.
name|util
operator|.
name|BytesRefIterator
import|;
end_import

begin_comment
comment|/**  * Collects {@link BytesRef} and then allows one to iterate over their sorted order. Implementations  * of this interface will be called in a single-threaded scenario.  * @lucene.experimental  * @lucene.internal    */
end_comment

begin_interface
DECL|interface|BytesRefSorter
specifier|public
interface|interface
name|BytesRefSorter
block|{
comment|/**    * Adds a single suggestion entry (possibly compound with its bucket).    *     * @throws IOException If an I/O exception occurs.    * @throws IllegalStateException If an addition attempt is performed after    * a call to {@link #iterator()} has been made.    */
DECL|method|add
name|void
name|add
parameter_list|(
name|BytesRef
name|utf8
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalStateException
function_decl|;
comment|/**    * Sorts the entries added in {@link #add(BytesRef)} and returns     * an iterator over all sorted entries.    *     * @throws IOException If an I/O exception occurs.    */
DECL|method|iterator
name|BytesRefIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Comparator used to determine the sort order of entries.    */
DECL|method|getComparator
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

