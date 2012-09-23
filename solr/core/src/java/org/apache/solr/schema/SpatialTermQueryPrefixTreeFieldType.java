begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|spatial
operator|.
name|prefix
operator|.
name|TermQueryPrefixTreeStrategy
import|;
end_import

begin_comment
comment|/**  * @see TermQueryPrefixTreeStrategy  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SpatialTermQueryPrefixTreeFieldType
specifier|public
class|class
name|SpatialTermQueryPrefixTreeFieldType
extends|extends
name|AbstractSpatialPrefixTreeFieldType
argument_list|<
name|TermQueryPrefixTreeStrategy
argument_list|>
block|{
annotation|@
name|Override
DECL|method|newPrefixTreeStrategy
specifier|protected
name|TermQueryPrefixTreeStrategy
name|newPrefixTreeStrategy
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|TermQueryPrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

