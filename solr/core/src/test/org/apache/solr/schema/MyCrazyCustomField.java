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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|IndexableField
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
name|Term
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
name|search
operator|.
name|PrefixQuery
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|SortField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|TextResponseWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|QParser
import|;
end_import

begin_comment
comment|/**  * Custom field that overrides the PrefixQuery behaviour to map queries such that:  * (foo* becomes bar*) and (bar* becomes foo*).  * This is used for testing overridden prefix query for custom fields in TestOverriddenPrefixQueryForCustomFieldType  */
end_comment

begin_class
DECL|class|MyCrazyCustomField
specifier|public
class|class
name|MyCrazyCustomField
extends|extends
name|TextField
block|{
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|IndexableField
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|writeStr
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
specifier|final
name|SchemaField
name|field
parameter_list|,
specifier|final
name|boolean
name|reverse
parameter_list|)
block|{
name|field
operator|.
name|checkSortability
argument_list|()
expr_stmt|;
return|return
name|getStringSort
argument_list|(
name|field
argument_list|,
name|reverse
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPrefixQuery
specifier|public
name|Query
name|getPrefixQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|sf
parameter_list|,
name|String
name|termStr
parameter_list|)
block|{
if|if
condition|(
name|termStr
operator|.
name|equals
argument_list|(
literal|"foo"
argument_list|)
condition|)
block|{
name|termStr
operator|=
literal|"bar"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|termStr
operator|.
name|equals
argument_list|(
literal|"bar"
argument_list|)
condition|)
block|{
name|termStr
operator|=
literal|"foo"
expr_stmt|;
block|}
name|PrefixQuery
name|query
init|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|,
name|termStr
argument_list|)
argument_list|)
decl_stmt|;
name|query
operator|.
name|setRewriteMethod
argument_list|(
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getRewriteMethod
argument_list|(
name|parser
argument_list|,
name|sf
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
block|}
end_class

end_unit

