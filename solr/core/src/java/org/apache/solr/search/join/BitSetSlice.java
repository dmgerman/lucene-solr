begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|join
package|;
end_package

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
name|FixedBitSet
import|;
end_import

begin_class
DECL|class|BitSetSlice
class|class
name|BitSetSlice
block|{
DECL|field|fbs
specifier|private
specifier|final
name|FixedBitSet
name|fbs
decl_stmt|;
DECL|field|off
specifier|private
specifier|final
name|int
name|off
decl_stmt|;
DECL|field|len
specifier|private
specifier|final
name|int
name|len
decl_stmt|;
DECL|method|BitSetSlice
name|BitSetSlice
parameter_list|(
name|FixedBitSet
name|fbs
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|this
operator|.
name|fbs
operator|=
name|fbs
expr_stmt|;
name|this
operator|.
name|off
operator|=
name|off
expr_stmt|;
name|this
operator|.
name|len
operator|=
name|len
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|fbs
operator|.
name|get
argument_list|(
name|pos
operator|+
name|off
argument_list|)
return|;
block|}
DECL|method|prevSetBit
specifier|public
name|int
name|prevSetBit
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|int
name|result
init|=
name|fbs
operator|.
name|prevSetBit
argument_list|(
name|pos
operator|+
name|off
argument_list|)
operator|-
name|off
decl_stmt|;
return|return
operator|(
name|result
operator|<
literal|0
operator|)
condition|?
operator|-
literal|1
else|:
name|result
return|;
block|}
DECL|method|nextSetBit
specifier|public
name|int
name|nextSetBit
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|int
name|result
init|=
name|fbs
operator|.
name|nextSetBit
argument_list|(
name|pos
operator|+
name|off
argument_list|)
operator|-
name|off
decl_stmt|;
return|return
operator|(
name|result
operator|>=
name|len
operator|)
condition|?
operator|-
literal|1
else|:
name|result
return|;
block|}
block|}
end_class

end_unit

