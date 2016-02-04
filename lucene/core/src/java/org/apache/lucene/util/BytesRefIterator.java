begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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

begin_comment
comment|/**  * A simple iterator interface for {@link BytesRef} iteration.  */
end_comment

begin_interface
DECL|interface|BytesRefIterator
specifier|public
interface|interface
name|BytesRefIterator
block|{
comment|/**    * Increments the iteration to the next {@link BytesRef} in the iterator.    * Returns the resulting {@link BytesRef} or<code>null</code> if the end of    * the iterator is reached. The returned BytesRef may be re-used across calls    * to next. After this method returns null, do not call it again: the results    * are undefined.    *     * @return the next {@link BytesRef} in the iterator or<code>null</code> if    *         the end of the iterator is reached.    * @throws IOException If there is a low-level I/O error.    */
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Singleton BytesRefIterator that iterates over 0 BytesRefs. */
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|BytesRefIterator
name|EMPTY
init|=
operator|new
name|BytesRefIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
block|}
end_interface

end_unit

