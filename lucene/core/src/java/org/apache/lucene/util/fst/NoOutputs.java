begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
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
name|store
operator|.
name|DataInput
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
name|store
operator|.
name|DataOutput
import|;
end_import

begin_comment
comment|/**  * A null FST {@link Outputs} implementation; use this if  * you just want to build an FSA.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|NoOutputs
specifier|public
specifier|final
class|class
name|NoOutputs
extends|extends
name|Outputs
argument_list|<
name|Object
argument_list|>
block|{
DECL|field|NO_OUTPUT
specifier|static
specifier|final
name|Object
name|NO_OUTPUT
init|=
operator|new
name|Object
argument_list|()
block|{
comment|// NodeHash calls hashCode for this output; we fix this
comment|// so we get deterministic hashing.
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|42
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|other
operator|==
name|this
return|;
block|}
block|}
decl_stmt|;
DECL|field|singleton
specifier|private
specifier|static
specifier|final
name|NoOutputs
name|singleton
init|=
operator|new
name|NoOutputs
argument_list|()
decl_stmt|;
DECL|method|NoOutputs
specifier|private
name|NoOutputs
parameter_list|()
block|{   }
DECL|method|getSingleton
specifier|public
specifier|static
name|NoOutputs
name|getSingleton
parameter_list|()
block|{
return|return
name|singleton
return|;
block|}
annotation|@
name|Override
DECL|method|common
specifier|public
name|Object
name|common
parameter_list|(
name|Object
name|output1
parameter_list|,
name|Object
name|output2
parameter_list|)
block|{
assert|assert
name|output1
operator|==
name|NO_OUTPUT
assert|;
assert|assert
name|output2
operator|==
name|NO_OUTPUT
assert|;
return|return
name|NO_OUTPUT
return|;
block|}
annotation|@
name|Override
DECL|method|subtract
specifier|public
name|Object
name|subtract
parameter_list|(
name|Object
name|output
parameter_list|,
name|Object
name|inc
parameter_list|)
block|{
assert|assert
name|output
operator|==
name|NO_OUTPUT
assert|;
assert|assert
name|inc
operator|==
name|NO_OUTPUT
assert|;
return|return
name|NO_OUTPUT
return|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|Object
name|add
parameter_list|(
name|Object
name|prefix
parameter_list|,
name|Object
name|output
parameter_list|)
block|{
assert|assert
name|prefix
operator|==
name|NO_OUTPUT
operator|:
literal|"got "
operator|+
name|prefix
assert|;
assert|assert
name|output
operator|==
name|NO_OUTPUT
assert|;
return|return
name|NO_OUTPUT
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Object
name|prefix
parameter_list|,
name|DataOutput
name|out
parameter_list|)
block|{
comment|//assert false;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|Object
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
block|{
comment|//assert false;
comment|//return null;
return|return
name|NO_OUTPUT
return|;
block|}
annotation|@
name|Override
DECL|method|getNoOutput
specifier|public
name|Object
name|getNoOutput
parameter_list|()
block|{
return|return
name|NO_OUTPUT
return|;
block|}
annotation|@
name|Override
DECL|method|outputToString
specifier|public
name|String
name|outputToString
parameter_list|(
name|Object
name|output
parameter_list|)
block|{
return|return
literal|""
return|;
block|}
block|}
end_class

end_unit

