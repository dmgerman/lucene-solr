begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.byTask.stats
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|stats
package|;
end_package

begin_comment
comment|/**  * Textual report of current statistics.  */
end_comment

begin_class
DECL|class|Report
specifier|public
class|class
name|Report
block|{
DECL|field|text
specifier|private
name|String
name|text
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|field|outOf
specifier|private
name|int
name|outOf
decl_stmt|;
DECL|field|reported
specifier|private
name|int
name|reported
decl_stmt|;
DECL|method|Report
specifier|public
name|Report
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|reported
parameter_list|,
name|int
name|outOf
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|reported
operator|=
name|reported
expr_stmt|;
name|this
operator|.
name|outOf
operator|=
name|outOf
expr_stmt|;
block|}
comment|/**    * Returns total number of stats points when this report was created.    */
DECL|method|getOutOf
specifier|public
name|int
name|getOutOf
parameter_list|()
block|{
return|return
name|outOf
return|;
block|}
comment|/**    * Returns number of lines in the report.    */
DECL|method|getSize
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**    * Returns the report text.    */
DECL|method|getText
specifier|public
name|String
name|getText
parameter_list|()
block|{
return|return
name|text
return|;
block|}
comment|/**    * Returns number of stats points represented in this report.    */
DECL|method|getReported
specifier|public
name|int
name|getReported
parameter_list|()
block|{
return|return
name|reported
return|;
block|}
block|}
end_class

end_unit

