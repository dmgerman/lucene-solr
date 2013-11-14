begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.simple
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|simple
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|LabelAndValue
specifier|public
specifier|final
class|class
name|LabelAndValue
block|{
comment|// nocommit BytesRef?
DECL|field|label
specifier|public
specifier|final
name|String
name|label
decl_stmt|;
comment|/** Value associated with this label. */
DECL|field|value
specifier|public
specifier|final
name|Number
name|value
decl_stmt|;
DECL|method|LabelAndValue
specifier|public
name|LabelAndValue
parameter_list|(
name|String
name|label
parameter_list|,
name|Number
name|value
parameter_list|)
block|{
name|this
operator|.
name|label
operator|=
name|label
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
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
name|label
operator|+
literal|" ("
operator|+
name|value
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

