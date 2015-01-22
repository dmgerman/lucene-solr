begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|// TODO: maybe TermsFilter could use this?
end_comment

begin_comment
comment|/** Iterates over terms in multiple fields, notifying the caller when a new field is started. */
end_comment

begin_interface
DECL|interface|FieldTermIterator
interface|interface
name|FieldTermIterator
block|{
comment|/** Advances to the next term, returning true if it's in a new field or there are no more terms.  Call {@link #field} to see which    *  field; if that returns null then the iteration ended. */
DECL|method|next
name|boolean
name|next
parameter_list|()
function_decl|;
comment|/** Returns current field, or null if the iteration ended. */
DECL|method|field
name|String
name|field
parameter_list|()
function_decl|;
comment|/** Returns current term. */
DECL|method|term
name|BytesRef
name|term
parameter_list|()
function_decl|;
comment|/** Del gen of the current term. */
comment|// TODO: this is really per-iterator not per term, but when we use MergedPrefixCodedTermsIterator we need to know which iterator we are on
DECL|method|delGen
name|long
name|delGen
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

