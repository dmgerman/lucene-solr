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
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * An {@link Iterator} implementation that filters elements with a boolean predicate.  *  * @param<T> generic parameter for this iterator instance: this iterator implements {@link Iterator Iterator&lt;T&gt;}  * @param<InnerT> generic parameter of the wrapped iterator, must be<tt>T</tt> or extend<tt>T</tt>  * @see #predicateFunction  * @lucene.internal  */
end_comment

begin_class
DECL|class|FilterIterator
specifier|public
specifier|abstract
class|class
name|FilterIterator
parameter_list|<
name|T
parameter_list|,
name|InnerT
extends|extends
name|T
parameter_list|>
implements|implements
name|Iterator
argument_list|<
name|T
argument_list|>
block|{
DECL|field|iterator
specifier|private
specifier|final
name|Iterator
argument_list|<
name|InnerT
argument_list|>
name|iterator
decl_stmt|;
DECL|field|next
specifier|private
name|T
name|next
init|=
literal|null
decl_stmt|;
DECL|field|nextIsSet
specifier|private
name|boolean
name|nextIsSet
init|=
literal|false
decl_stmt|;
comment|/** returns true, if this element should be returned by {@link #next()}. */
DECL|method|predicateFunction
specifier|protected
specifier|abstract
name|boolean
name|predicateFunction
parameter_list|(
name|InnerT
name|object
parameter_list|)
function_decl|;
DECL|method|FilterIterator
specifier|public
name|FilterIterator
parameter_list|(
name|Iterator
argument_list|<
name|InnerT
argument_list|>
name|baseIterator
parameter_list|)
block|{
name|this
operator|.
name|iterator
operator|=
name|baseIterator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
specifier|final
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nextIsSet
operator|||
name|setNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
specifier|final
name|T
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
assert|assert
name|nextIsSet
assert|;
try|try
block|{
return|return
name|next
return|;
block|}
finally|finally
block|{
name|nextIsSet
operator|=
literal|false
expr_stmt|;
name|next
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
specifier|final
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|setNext
specifier|private
name|boolean
name|setNext
parameter_list|()
block|{
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|InnerT
name|object
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|predicateFunction
argument_list|(
name|object
argument_list|)
condition|)
block|{
name|next
operator|=
name|object
expr_stmt|;
name|nextIsSet
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

