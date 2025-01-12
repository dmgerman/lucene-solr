begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_comment
comment|/**   * Throws IOException from random Tokenstream methods.  *<p>  * This can be used to simulate a buggy analyzer in IndexWriter,  * where we must delete the document but not abort everything in the buffer.  */
end_comment

begin_class
DECL|class|CrankyTokenFilter
specifier|public
specifier|final
class|class
name|CrankyTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|thingToDo
name|int
name|thingToDo
decl_stmt|;
comment|/** Creates a new CrankyTokenFilter */
DECL|method|CrankyTokenFilter
specifier|public
name|CrankyTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
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
name|thingToDo
operator|==
literal|0
operator|&&
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException from TokenStream.incrementToken()"
argument_list|)
throw|;
block|}
return|return
name|input
operator|.
name|incrementToken
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|end
argument_list|()
expr_stmt|;
if|if
condition|(
name|thingToDo
operator|==
literal|1
operator|&&
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException from TokenStream.end()"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|thingToDo
operator|=
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
expr_stmt|;
if|if
condition|(
name|thingToDo
operator|==
literal|2
operator|&&
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException from TokenStream.reset()"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|thingToDo
operator|==
literal|3
operator|&&
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException from TokenStream.close()"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

