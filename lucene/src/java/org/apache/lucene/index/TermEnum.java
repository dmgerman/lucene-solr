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
name|io
operator|.
name|Closeable
import|;
end_import

begin_comment
comment|/** Abstract class for enumerating terms.<p>Term enumerations are always ordered by Term.compareTo().  Each term in   the enumeration is greater than all that precede it. * @deprecated Use TermsEnum instead */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|TermEnum
specifier|public
specifier|abstract
class|class
name|TermEnum
implements|implements
name|Closeable
block|{
comment|/** Increments the enumeration to the next element.  True if one exists.*/
DECL|method|next
specifier|public
specifier|abstract
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the current Term in the enumeration.*/
DECL|method|term
specifier|public
specifier|abstract
name|Term
name|term
parameter_list|()
function_decl|;
comment|/** Returns the docFreq of the current Term in the enumeration.*/
DECL|method|docFreq
specifier|public
specifier|abstract
name|int
name|docFreq
parameter_list|()
function_decl|;
comment|/** Closes the enumeration to further activity, freeing resources. */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

