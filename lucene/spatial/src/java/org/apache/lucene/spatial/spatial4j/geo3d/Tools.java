begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.spatial4j.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|spatial4j
operator|.
name|geo3d
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Static methods globally useful for 3d geometric work.  */
end_comment

begin_class
DECL|class|Tools
specifier|public
class|class
name|Tools
block|{
DECL|method|Tools
specifier|private
name|Tools
parameter_list|()
block|{   }
comment|/**    * Java acos yields a NAN if you take an arc-cos of an    * angle that's just a tiny bit greater than 1.0, so    * here's a more resilient version.    */
DECL|method|safeAcos
specifier|public
specifier|static
name|double
name|safeAcos
parameter_list|(
name|double
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|>
literal|1.0
condition|)
name|value
operator|=
literal|1.0
expr_stmt|;
elseif|else
if|if
condition|(
name|value
operator|<
operator|-
literal|1.0
condition|)
name|value
operator|=
operator|-
literal|1.0
expr_stmt|;
return|return
name|Math
operator|.
name|acos
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

