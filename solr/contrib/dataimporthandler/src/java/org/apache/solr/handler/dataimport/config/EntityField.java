begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport.config
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|config
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|SEVERE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|ConfigParseUtil
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
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
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
name|handler
operator|.
name|dataimport
operator|.
name|DataImporter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_class
DECL|class|EntityField
specifier|public
class|class
name|EntityField
block|{
DECL|field|column
specifier|private
specifier|final
name|String
name|column
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|boost
specifier|private
specifier|final
name|float
name|boost
decl_stmt|;
DECL|field|toWrite
specifier|private
specifier|final
name|boolean
name|toWrite
decl_stmt|;
DECL|field|multiValued
specifier|private
specifier|final
name|boolean
name|multiValued
decl_stmt|;
DECL|field|dynamicName
specifier|private
specifier|final
name|boolean
name|dynamicName
decl_stmt|;
DECL|field|entity
specifier|private
specifier|final
name|Entity
name|entity
decl_stmt|;
DECL|field|allAttributes
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|allAttributes
decl_stmt|;
DECL|method|EntityField
specifier|public
name|EntityField
parameter_list|(
name|Builder
name|b
parameter_list|)
block|{
name|this
operator|.
name|column
operator|=
name|b
operator|.
name|column
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|b
operator|.
name|name
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|b
operator|.
name|boost
expr_stmt|;
name|this
operator|.
name|toWrite
operator|=
name|b
operator|.
name|toWrite
expr_stmt|;
name|this
operator|.
name|multiValued
operator|=
name|b
operator|.
name|multiValued
expr_stmt|;
name|this
operator|.
name|dynamicName
operator|=
name|b
operator|.
name|dynamicName
expr_stmt|;
name|this
operator|.
name|entity
operator|=
name|b
operator|.
name|entity
expr_stmt|;
name|this
operator|.
name|allAttributes
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|b
operator|.
name|allAttributes
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
operator|==
literal|null
condition|?
name|column
else|:
name|name
return|;
block|}
DECL|method|getEntity
specifier|public
name|Entity
name|getEntity
parameter_list|()
block|{
return|return
name|entity
return|;
block|}
DECL|method|getColumn
specifier|public
name|String
name|getColumn
parameter_list|()
block|{
return|return
name|column
return|;
block|}
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
DECL|method|isToWrite
specifier|public
name|boolean
name|isToWrite
parameter_list|()
block|{
return|return
name|toWrite
return|;
block|}
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
name|multiValued
return|;
block|}
DECL|method|isDynamicName
specifier|public
name|boolean
name|isDynamicName
parameter_list|()
block|{
return|return
name|dynamicName
return|;
block|}
DECL|method|getAllAttributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAllAttributes
parameter_list|()
block|{
return|return
name|allAttributes
return|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|column
specifier|public
name|String
name|column
decl_stmt|;
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
DECL|field|boost
specifier|public
name|float
name|boost
decl_stmt|;
DECL|field|toWrite
specifier|public
name|boolean
name|toWrite
init|=
literal|true
decl_stmt|;
DECL|field|multiValued
specifier|public
name|boolean
name|multiValued
init|=
literal|false
decl_stmt|;
DECL|field|dynamicName
specifier|public
name|boolean
name|dynamicName
init|=
literal|false
decl_stmt|;
DECL|field|entity
specifier|public
name|Entity
name|entity
decl_stmt|;
DECL|field|allAttributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|allAttributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|Element
name|e
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|ConfigParseUtil
operator|.
name|getStringAttribute
argument_list|(
name|e
argument_list|,
name|DataImporter
operator|.
name|NAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|column
operator|=
name|ConfigParseUtil
operator|.
name|getStringAttribute
argument_list|(
name|e
argument_list|,
name|DataImporter
operator|.
name|COLUMN
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|column
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"Field must have a column attribute"
argument_list|)
throw|;
block|}
name|this
operator|.
name|boost
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|ConfigParseUtil
operator|.
name|getStringAttribute
argument_list|(
name|e
argument_list|,
literal|"boost"
argument_list|,
literal|"1.0f"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|allAttributes
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|ConfigParseUtil
operator|.
name|getAllAttributes
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getNameOrColumn
specifier|public
name|String
name|getNameOrColumn
parameter_list|()
block|{
return|return
name|name
operator|==
literal|null
condition|?
name|column
else|:
name|name
return|;
block|}
block|}
block|}
end_class

end_unit

