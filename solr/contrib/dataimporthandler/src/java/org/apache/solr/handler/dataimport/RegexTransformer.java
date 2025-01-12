begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
comment|/**  *<p>  * A {@link Transformer} implementation which uses Regular Expressions to extract, split  * and replace data in fields.  *</p>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p>  *<b>This API is experimental and may change in the future.</b>  *  * @since solr 1.3  * @see Pattern  */
end_comment

begin_class
DECL|class|RegexTransformer
specifier|public
class|class
name|RegexTransformer
extends|extends
name|Transformer
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
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|transformRow
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|,
name|Context
name|ctx
parameter_list|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|fields
init|=
name|ctx
operator|.
name|getAllEntityFields
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|field
range|:
name|fields
control|)
block|{
name|String
name|col
init|=
name|field
operator|.
name|get
argument_list|(
name|DataImporter
operator|.
name|COLUMN
argument_list|)
decl_stmt|;
name|String
name|reStr
init|=
name|ctx
operator|.
name|replaceTokens
argument_list|(
name|field
operator|.
name|get
argument_list|(
name|REGEX
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|splitBy
init|=
name|ctx
operator|.
name|replaceTokens
argument_list|(
name|field
operator|.
name|get
argument_list|(
name|SPLIT_BY
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|replaceWith
init|=
name|ctx
operator|.
name|replaceTokens
argument_list|(
name|field
operator|.
name|get
argument_list|(
name|REPLACE_WITH
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|groupNames
init|=
name|ctx
operator|.
name|replaceTokens
argument_list|(
name|field
operator|.
name|get
argument_list|(
name|GROUP_NAMES
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|reStr
operator|!=
literal|null
operator|||
name|splitBy
operator|!=
literal|null
condition|)
block|{
name|String
name|srcColName
init|=
name|field
operator|.
name|get
argument_list|(
name|SRC_COL_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcColName
operator|==
literal|null
condition|)
block|{
name|srcColName
operator|=
name|col
expr_stmt|;
block|}
name|Object
name|tmpVal
init|=
name|row
operator|.
name|get
argument_list|(
name|srcColName
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmpVal
operator|==
literal|null
condition|)
continue|continue;
if|if
condition|(
name|tmpVal
operator|instanceof
name|List
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|inputs
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|tmpVal
decl_stmt|;
name|List
name|results
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|>
name|otherVars
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|input
range|:
name|inputs
control|)
block|{
name|Object
name|o
init|=
name|process
argument_list|(
name|col
argument_list|,
name|reStr
argument_list|,
name|splitBy
argument_list|,
name|replaceWith
argument_list|,
name|input
argument_list|,
name|groupNames
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|o
decl_stmt|;
for|for
control|(
name|Object
name|e
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|e
decl_stmt|;
name|List
name|l
init|=
name|results
decl_stmt|;
if|if
condition|(
operator|!
name|col
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|otherVars
operator|==
literal|null
condition|)
name|otherVars
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|l
operator|=
name|otherVars
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
name|l
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|otherVars
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|instanceof
name|Collection
condition|)
block|{
name|l
operator|.
name|addAll
argument_list|(
operator|(
name|Collection
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|l
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|o
operator|instanceof
name|Collection
condition|)
block|{
name|results
operator|.
name|addAll
argument_list|(
operator|(
name|Collection
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|results
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|row
operator|.
name|put
argument_list|(
name|col
argument_list|,
name|results
argument_list|)
expr_stmt|;
if|if
condition|(
name|otherVars
operator|!=
literal|null
condition|)
name|row
operator|.
name|putAll
argument_list|(
name|otherVars
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|value
init|=
name|tmpVal
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Object
name|o
init|=
name|process
argument_list|(
name|col
argument_list|,
name|reStr
argument_list|,
name|splitBy
argument_list|,
name|replaceWith
argument_list|,
name|value
argument_list|,
name|groupNames
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
name|row
operator|.
name|putAll
argument_list|(
operator|(
name|Map
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|row
operator|.
name|put
argument_list|(
name|col
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|row
return|;
block|}
DECL|method|process
specifier|private
name|Object
name|process
parameter_list|(
name|String
name|col
parameter_list|,
name|String
name|reStr
parameter_list|,
name|String
name|splitBy
parameter_list|,
name|String
name|replaceWith
parameter_list|,
name|String
name|value
parameter_list|,
name|String
name|groupNames
parameter_list|)
block|{
if|if
condition|(
name|splitBy
operator|!=
literal|null
condition|)
block|{
return|return
name|readBySplit
argument_list|(
name|splitBy
argument_list|,
name|value
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|replaceWith
operator|!=
literal|null
condition|)
block|{
name|Pattern
name|p
init|=
name|getPattern
argument_list|(
name|reStr
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
name|m
operator|.
name|find
argument_list|()
condition|?
name|m
operator|.
name|replaceAll
argument_list|(
name|replaceWith
argument_list|)
else|:
name|value
return|;
block|}
else|else
block|{
return|return
name|readfromRegExp
argument_list|(
name|reStr
argument_list|,
name|value
argument_list|,
name|col
argument_list|,
name|groupNames
argument_list|)
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|readBySplit
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|readBySplit
parameter_list|(
name|String
name|splitBy
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|String
index|[]
name|vals
init|=
name|value
operator|.
name|split
argument_list|(
name|splitBy
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|l
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|vals
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|l
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|readfromRegExp
specifier|private
name|Object
name|readfromRegExp
parameter_list|(
name|String
name|reStr
parameter_list|,
name|String
name|value
parameter_list|,
name|String
name|columnName
parameter_list|,
name|String
name|gNames
parameter_list|)
block|{
name|String
index|[]
name|groupNames
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|gNames
operator|!=
literal|null
operator|&&
name|gNames
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|groupNames
operator|=
name|gNames
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|Pattern
name|regexp
init|=
name|getPattern
argument_list|(
name|reStr
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|regexp
operator|.
name|matcher
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
operator|&&
name|m
operator|.
name|groupCount
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|m
operator|.
name|groupCount
argument_list|()
operator|>
literal|1
condition|)
block|{
name|List
name|l
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|groupNames
operator|==
literal|null
condition|)
block|{
name|l
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|map
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|m
operator|.
name|groupCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
if|if
condition|(
name|l
operator|!=
literal|null
condition|)
block|{
name|l
operator|.
name|add
argument_list|(
name|m
operator|.
name|group
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|i
operator|<=
name|groupNames
operator|.
name|length
condition|)
block|{
name|String
name|nameOfGroup
init|=
name|groupNames
index|[
name|i
operator|-
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|nameOfGroup
operator|!=
literal|null
operator|&&
name|nameOfGroup
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|nameOfGroup
argument_list|,
name|m
operator|.
name|group
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Parsing failed for field : "
operator|+
name|columnName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|l
operator|==
literal|null
condition|?
name|map
else|:
name|l
return|;
block|}
else|else
block|{
return|return
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getPattern
specifier|private
name|Pattern
name|getPattern
parameter_list|(
name|String
name|reStr
parameter_list|)
block|{
name|Pattern
name|result
init|=
name|PATTERN_CACHE
operator|.
name|get
argument_list|(
name|reStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|PATTERN_CACHE
operator|.
name|put
argument_list|(
name|reStr
argument_list|,
name|result
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|reStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|field|PATTERN_CACHE
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|Pattern
argument_list|>
name|PATTERN_CACHE
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|REGEX
specifier|public
specifier|static
specifier|final
name|String
name|REGEX
init|=
literal|"regex"
decl_stmt|;
DECL|field|REPLACE_WITH
specifier|public
specifier|static
specifier|final
name|String
name|REPLACE_WITH
init|=
literal|"replaceWith"
decl_stmt|;
DECL|field|SPLIT_BY
specifier|public
specifier|static
specifier|final
name|String
name|SPLIT_BY
init|=
literal|"splitBy"
decl_stmt|;
DECL|field|SRC_COL_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SRC_COL_NAME
init|=
literal|"sourceColName"
decl_stmt|;
DECL|field|GROUP_NAMES
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_NAMES
init|=
literal|"groupNames"
decl_stmt|;
block|}
end_class

end_unit

