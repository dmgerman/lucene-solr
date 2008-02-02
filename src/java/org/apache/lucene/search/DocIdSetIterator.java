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

begin_comment
comment|/**  * This abstract class defines methods to iterate over a set of  * non-decreasing doc ids.  */
end_comment

begin_class
DECL|class|DocIdSetIterator
specifier|public
specifier|abstract
class|class
name|DocIdSetIterator
block|{
comment|/** Returns the current document number.<p> This is invalid until {@link     #next()} is called for the first time.*/
DECL|method|doc
specifier|public
specifier|abstract
name|int
name|doc
parameter_list|()
function_decl|;
comment|/** Moves to the next docId in the set. Returns true, iff      * there is such a docId. */
DECL|method|next
specifier|public
specifier|abstract
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Skips entries to the first beyond the current whose document number is      * greater than or equal to<i>target</i>.<p>Returns true iff there is such      * an entry.<p>Behaves as if written:<pre>      *   boolean skipTo(int target) {      *     do {      *       if (!next())      *         return false;      *     } while (target> doc());      *     return true;      *   }      *</pre>      * Some implementations are considerably more efficient than that.      */
DECL|method|skipTo
specifier|public
specifier|abstract
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

