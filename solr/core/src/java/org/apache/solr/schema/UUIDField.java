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
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|common
operator|.
name|SolrException
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
name|update
operator|.
name|processor
operator|.
name|UUIDUpdateProcessorFactory
import|;
end_import

begin_comment
comment|// jdoc
end_comment

begin_comment
comment|/**  *<p>  * This FieldType accepts UUID string values, as well as the special value   * of "NEW" which triggers generation of a new random UUID.  *</p>  *<p>  *<b>NOTE:</b> Configuring a<code>UUIDField</code>   * instance with a default value of "<code>NEW</code>" is not advisable for   * most users when using SolrCloud (and not possible if the UUID value is   * configured as the unique key field) since the result will be that each   * replica of each document will get a unique UUID value.    * Using {@link UUIDUpdateProcessorFactory} to generate UUID values when   * documents are added is recommended instead.  *</p>  *   * @see UUID#toString  * @see UUID#randomUUID  *  */
end_comment

begin_class
DECL|class|UUIDField
specifier|public
class|class
name|UUIDField
extends|extends
name|StrField
block|{
DECL|field|NEW
specifier|private
specifier|static
specifier|final
name|String
name|NEW
init|=
literal|"NEW"
decl_stmt|;
DECL|field|DASH
specifier|private
specifier|static
specifier|final
name|char
name|DASH
init|=
literal|'-'
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
comment|// Tokenizing makes no sense
name|restrictProps
argument_list|(
name|TOKENIZED
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
name|SchemaField
name|field
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
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
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Generates a UUID if val is either null, empty or "NEW".    *     * Otherwise it behaves much like a StrField but checks that the value given    * is indeed a valid UUID.    *     * @param val The value of the field    * @see org.apache.solr.schema.FieldType#toInternal(java.lang.String)    */
annotation|@
name|Override
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|==
literal|null
operator|||
literal|0
operator|==
name|val
operator|.
name|length
argument_list|()
operator|||
name|NEW
operator|.
name|equals
argument_list|(
name|val
argument_list|)
condition|)
block|{
return|return
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
else|else
block|{
comment|// we do some basic validation if 'val' looks like an UUID
if|if
condition|(
name|val
operator|.
name|length
argument_list|()
operator|!=
literal|36
operator|||
name|val
operator|.
name|charAt
argument_list|(
literal|8
argument_list|)
operator|!=
name|DASH
operator|||
name|val
operator|.
name|charAt
argument_list|(
literal|13
argument_list|)
operator|!=
name|DASH
operator|||
name|val
operator|.
name|charAt
argument_list|(
literal|18
argument_list|)
operator|!=
name|DASH
operator|||
name|val
operator|.
name|charAt
argument_list|(
literal|23
argument_list|)
operator|!=
name|DASH
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid UUID String: '"
operator|+
name|val
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
name|val
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
block|}
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|UUID
name|uuid
parameter_list|)
block|{
return|return
name|uuid
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|UUID
name|toObject
parameter_list|(
name|IndexableField
name|f
parameter_list|)
block|{
return|return
name|UUID
operator|.
name|fromString
argument_list|(
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

