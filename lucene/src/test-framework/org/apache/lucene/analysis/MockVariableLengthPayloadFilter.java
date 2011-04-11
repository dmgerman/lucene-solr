begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|Random
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PayloadAttribute
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
name|Payload
import|;
end_import

begin_class
DECL|class|MockVariableLengthPayloadFilter
specifier|final
class|class
name|MockVariableLengthPayloadFilter
extends|extends
name|TokenFilter
block|{
DECL|field|MAXLENGTH
specifier|private
specifier|static
specifier|final
name|int
name|MAXLENGTH
init|=
literal|129
decl_stmt|;
DECL|field|payloadAtt
specifier|private
specifier|final
name|PayloadAttribute
name|payloadAtt
init|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|bytes
specifier|private
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|MAXLENGTH
index|]
decl_stmt|;
DECL|field|payload
specifier|private
specifier|final
name|Payload
name|payload
decl_stmt|;
DECL|method|MockVariableLengthPayloadFilter
name|MockVariableLengthPayloadFilter
parameter_list|(
name|Random
name|random
parameter_list|,
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|payload
operator|=
operator|new
name|Payload
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|random
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|payload
operator|.
name|setData
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|random
operator|.
name|nextInt
argument_list|(
name|MAXLENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|payloadAtt
operator|.
name|setPayload
argument_list|(
name|payload
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

