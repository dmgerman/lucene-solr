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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/** Abstract class for enumerating terms.<p>Term enumerations are always ordered by Term.compareTo().  Each term in   the enumeration is greater than all that precede it.  */
end_comment

begin_class
DECL|class|TermEnum
specifier|public
specifier|abstract
class|class
name|TermEnum
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
comment|// Term Vector support
comment|/** Skips terms to the first beyond the current whose value is    * greater or equal to<i>target</i>.<p>Returns true iff there is such    * an entry.<p>Behaves as if written:<pre>    *   public boolean skipTo(Term target) {    *     do {    *       if (!next())    * 	     return false;    *     } while (target> term());    *     return true;    *   }    *</pre>    * Some implementations are considerably more efficient than that.    */
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|Term
name|target
parameter_list|)
throws|throws
name|IOException
block|{
do|do
block|{
if|if
condition|(
operator|!
name|next
argument_list|()
condition|)
return|return
literal|false
return|;
block|}
do|while
condition|(
name|target
operator|.
name|compareTo
argument_list|(
name|term
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
do|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

