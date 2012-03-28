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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ToStringUtils
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  *   Only return those matches that have a specific payload at  *  the given position.  *<p/>  * Do not use this with an SpanQuery that contains a {@link org.apache.lucene.search.spans.SpanNearQuery}.  Instead, use  * {@link SpanNearPayloadCheckQuery} since it properly handles the fact that payloads  * aren't ordered by {@link org.apache.lucene.search.spans.SpanNearQuery}.  *  **/
end_comment

begin_class
DECL|class|SpanPayloadCheckQuery
specifier|public
class|class
name|SpanPayloadCheckQuery
extends|extends
name|SpanPositionCheckQuery
block|{
DECL|field|payloadToMatch
specifier|protected
specifier|final
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|payloadToMatch
decl_stmt|;
comment|/**    *    * @param match The underlying {@link org.apache.lucene.search.spans.SpanQuery} to check    * @param payloadToMatch The {@link java.util.Collection} of payloads to match    */
DECL|method|SpanPayloadCheckQuery
specifier|public
name|SpanPayloadCheckQuery
parameter_list|(
name|SpanQuery
name|match
parameter_list|,
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|payloadToMatch
parameter_list|)
block|{
name|super
argument_list|(
name|match
argument_list|)
expr_stmt|;
if|if
condition|(
name|match
operator|instanceof
name|SpanNearQuery
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"SpanNearQuery not allowed"
argument_list|)
throw|;
block|}
name|this
operator|.
name|payloadToMatch
operator|=
name|payloadToMatch
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptPosition
specifier|protected
name|AcceptStatus
name|acceptPosition
parameter_list|(
name|Spans
name|spans
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|result
init|=
name|spans
operator|.
name|isPayloadAvailable
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|true
condition|)
block|{
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|candidate
init|=
name|spans
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|candidate
operator|.
name|size
argument_list|()
operator|==
name|payloadToMatch
operator|.
name|size
argument_list|()
condition|)
block|{
comment|//TODO: check the byte arrays are the same
name|Iterator
argument_list|<
name|byte
index|[]
argument_list|>
name|toMatchIter
init|=
name|payloadToMatch
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|//check each of the byte arrays, in order
comment|//hmm, can't rely on order here
for|for
control|(
name|byte
index|[]
name|candBytes
range|:
name|candidate
control|)
block|{
comment|//if one is a mismatch, then return false
if|if
condition|(
name|Arrays
operator|.
name|equals
argument_list|(
name|candBytes
argument_list|,
name|toMatchIter
operator|.
name|next
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|NO
return|;
block|}
block|}
comment|//we've verified all the bytes
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
else|else
block|{
return|return
name|AcceptStatus
operator|.
name|NO
return|;
block|}
block|}
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"spanPayCheck("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|match
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", payloadRef: "
argument_list|)
expr_stmt|;
for|for
control|(
name|byte
index|[]
name|bytes
range|:
name|payloadToMatch
control|)
block|{
name|ToStringUtils
operator|.
name|byteArray
argument_list|(
name|buffer
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SpanPayloadCheckQuery
name|clone
parameter_list|()
block|{
name|SpanPayloadCheckQuery
name|result
init|=
operator|new
name|SpanPayloadCheckQuery
argument_list|(
operator|(
name|SpanQuery
operator|)
name|match
operator|.
name|clone
argument_list|()
argument_list|,
name|payloadToMatch
argument_list|)
decl_stmt|;
name|result
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|SpanPayloadCheckQuery
operator|)
condition|)
return|return
literal|false
return|;
name|SpanPayloadCheckQuery
name|other
init|=
operator|(
name|SpanPayloadCheckQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|payloadToMatch
operator|.
name|equals
argument_list|(
name|other
operator|.
name|payloadToMatch
argument_list|)
operator|&&
name|this
operator|.
name|match
operator|.
name|equals
argument_list|(
name|other
operator|.
name|match
argument_list|)
operator|&&
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|match
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|^=
operator|(
name|h
operator|<<
literal|8
operator|)
operator||
operator|(
name|h
operator|>>>
literal|25
operator|)
expr_stmt|;
comment|// reversible
comment|//TODO: is this right?
name|h
operator|^=
name|payloadToMatch
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|^=
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class

end_unit

