begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Normalization model that assumes a uniform distribution of the term frequency.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|NormalizationH1
specifier|public
class|class
name|NormalizationH1
extends|extends
name|Normalization
block|{
annotation|@
name|Override
DECL|method|tfn
specifier|public
specifier|final
name|float
name|tfn
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|tf
parameter_list|,
name|float
name|len
parameter_list|)
block|{
return|return
name|tf
operator|*
name|stats
operator|.
name|getAvgFieldLength
argument_list|()
operator|/
name|len
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"1"
return|;
block|}
block|}
end_class

end_unit

