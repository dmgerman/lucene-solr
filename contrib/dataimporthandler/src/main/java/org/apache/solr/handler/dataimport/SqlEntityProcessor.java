begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  *<p>  * An EntityProcessor instance which provides support for reading from  * databases. It is used in conjunction with JdbcDataSource. This is the default  * EntityProcessor if none is specified explicitly in data-config.xml  *</p>  *<p/>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|SqlEntityProcessor
specifier|public
class|class
name|SqlEntityProcessor
extends|extends
name|EntityProcessorBase
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SqlEntityProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dataSource
specifier|protected
name|DataSource
argument_list|<
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
name|dataSource
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|dataSource
operator|=
name|context
operator|.
name|getDataSource
argument_list|()
expr_stmt|;
block|}
DECL|method|initQuery
specifier|protected
name|void
name|initQuery
parameter_list|(
name|String
name|q
parameter_list|)
block|{
try|try
block|{
name|DataImporter
operator|.
name|QUERY_COUNT
operator|.
name|get
argument_list|()
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|rowIterator
operator|=
name|dataSource
operator|.
name|getData
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|q
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DataImportHandlerException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"The query failed '"
operator|+
name|q
operator|+
literal|"'"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|nextRow
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextRow
parameter_list|()
block|{
if|if
condition|(
name|rowIterator
operator|==
literal|null
condition|)
block|{
name|String
name|q
init|=
name|getQuery
argument_list|()
decl_stmt|;
name|initQuery
argument_list|(
name|resolver
operator|.
name|replaceTokens
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|getNext
argument_list|()
return|;
block|}
DECL|method|nextModifiedRowKey
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextModifiedRowKey
parameter_list|()
block|{
if|if
condition|(
name|rowIterator
operator|==
literal|null
condition|)
block|{
name|String
name|deltaQuery
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|DELTA_QUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|deltaQuery
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|initQuery
argument_list|(
name|resolver
operator|.
name|replaceTokens
argument_list|(
name|deltaQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|getNext
argument_list|()
return|;
block|}
DECL|method|nextDeletedRowKey
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextDeletedRowKey
parameter_list|()
block|{
if|if
condition|(
name|rowIterator
operator|==
literal|null
condition|)
block|{
name|String
name|deletedPkQuery
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|DEL_PK_QUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|deletedPkQuery
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|initQuery
argument_list|(
name|resolver
operator|.
name|replaceTokens
argument_list|(
name|deletedPkQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|getNext
argument_list|()
return|;
block|}
DECL|method|nextModifiedParentRowKey
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextModifiedParentRowKey
parameter_list|()
block|{
if|if
condition|(
name|rowIterator
operator|==
literal|null
condition|)
block|{
name|String
name|parentDeltaQuery
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|PARENT_DELTA_QUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentDeltaQuery
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Running parentDeltaQuery for Entity: "
operator|+
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|initQuery
argument_list|(
name|resolver
operator|.
name|replaceTokens
argument_list|(
name|parentDeltaQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|getNext
argument_list|()
return|;
block|}
DECL|method|getQuery
specifier|public
name|String
name|getQuery
parameter_list|()
block|{
name|String
name|queryString
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|QUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|Context
operator|.
name|FULL_DUMP
operator|.
name|equals
argument_list|(
name|context
operator|.
name|currentProcess
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|queryString
return|;
block|}
if|if
condition|(
name|Context
operator|.
name|DELTA_DUMP
operator|.
name|equals
argument_list|(
name|context
operator|.
name|currentProcess
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|deltaImportQuery
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|DELTA_IMPORT_QUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|deltaImportQuery
operator|!=
literal|null
condition|)
return|return
name|deltaImportQuery
return|;
block|}
return|return
name|getDeltaImportQuery
argument_list|(
name|queryString
argument_list|)
return|;
block|}
DECL|method|getDeltaImportQuery
specifier|public
name|String
name|getDeltaImportQuery
parameter_list|(
name|String
name|queryString
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|queryString
argument_list|)
decl_stmt|;
if|if
condition|(
name|SELECT_WHERE_PATTERN
operator|.
name|matcher
argument_list|(
name|queryString
argument_list|)
operator|.
name|find
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" and "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" where "
argument_list|)
expr_stmt|;
block|}
name|boolean
name|first
init|=
literal|true
decl_stmt|;
name|String
index|[]
name|primaryKeys
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"pk"
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|primaryKey
range|:
name|primaryKeys
control|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" and "
argument_list|)
expr_stmt|;
block|}
name|first
operator|=
literal|false
expr_stmt|;
name|Object
name|val
init|=
name|resolver
operator|.
name|resolve
argument_list|(
literal|"dataimporter.delta."
operator|+
name|primaryKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|Matcher
name|m
init|=
name|DOT_PATTERN
operator|.
name|matcher
argument_list|(
name|primaryKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|val
operator|=
name|resolver
operator|.
name|resolve
argument_list|(
literal|"dataimporter.delta."
operator|+
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
name|primaryKey
argument_list|)
operator|.
name|append
argument_list|(
literal|" = "
argument_list|)
expr_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|Number
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"'"
argument_list|)
operator|.
name|append
argument_list|(
name|val
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|field|SELECT_WHERE_PATTERN
specifier|private
specifier|static
name|Pattern
name|SELECT_WHERE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\s*(select\\b.*?\\b)(where).*"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
DECL|field|QUERY
specifier|public
specifier|static
specifier|final
name|String
name|QUERY
init|=
literal|"query"
decl_stmt|;
DECL|field|DELTA_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|DELTA_QUERY
init|=
literal|"deltaQuery"
decl_stmt|;
DECL|field|DELTA_IMPORT_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|DELTA_IMPORT_QUERY
init|=
literal|"deltaImportQuery"
decl_stmt|;
DECL|field|PARENT_DELTA_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|PARENT_DELTA_QUERY
init|=
literal|"parentDeltaQuery"
decl_stmt|;
DECL|field|DEL_PK_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|DEL_PK_QUERY
init|=
literal|"deletedPkQuery"
decl_stmt|;
DECL|field|DOT_PATTERN
specifier|public
specifier|static
specifier|final
name|Pattern
name|DOT_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*?\\.(.*)$"
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

