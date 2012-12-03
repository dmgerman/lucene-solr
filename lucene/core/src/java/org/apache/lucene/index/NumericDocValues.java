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

begin_class
DECL|class|NumericDocValues
specifier|public
specifier|abstract
class|class
name|NumericDocValues
block|{
DECL|method|get
specifier|public
specifier|abstract
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
function_decl|;
DECL|method|minValue
specifier|public
specifier|abstract
name|long
name|minValue
parameter_list|()
function_decl|;
DECL|method|maxValue
specifier|public
specifier|abstract
name|long
name|maxValue
parameter_list|()
function_decl|;
DECL|method|size
specifier|public
specifier|abstract
name|int
name|size
parameter_list|()
function_decl|;
DECL|class|EMPTY
specifier|public
specifier|static
specifier|final
class|class
name|EMPTY
extends|extends
name|NumericDocValues
block|{
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|method|EMPTY
specifier|public
name|EMPTY
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|minValue
specifier|public
name|long
name|minValue
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|maxValue
specifier|public
name|long
name|maxValue
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
block|}
empty_stmt|;
block|}
end_class

end_unit

