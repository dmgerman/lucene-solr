begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FilenameUtils
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
name|queries
operator|.
name|function
operator|.
name|FunctionQuery
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|QueryValueSource
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
name|common
operator|.
name|SolrException
operator|.
name|ErrorCode
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
name|params
operator|.
name|CommonParams
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
name|params
operator|.
name|ModifiableSolrParams
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
name|params
operator|.
name|SolrParams
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
name|util
operator|.
name|NamedList
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
name|request
operator|.
name|SolrQueryRequest
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
name|transform
operator|.
name|DocTransformer
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
name|transform
operator|.
name|DocTransformers
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
name|transform
operator|.
name|RenameFieldTransformer
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
name|transform
operator|.
name|ScoreAugmenter
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
name|transform
operator|.
name|TransformerFactory
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
name|transform
operator|.
name|ValueSourceAugmenter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * The default implementation of return fields parsing for Solr.  */
end_comment

begin_class
DECL|class|SolrReturnFields
specifier|public
class|class
name|SolrReturnFields
extends|extends
name|ReturnFields
block|{
comment|// Special Field Keys
DECL|field|SCORE
specifier|public
specifier|static
specifier|final
name|String
name|SCORE
init|=
literal|"score"
decl_stmt|;
DECL|field|globs
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|globs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// The lucene field names to request from the SolrIndexSearcher
comment|// This *may* include fields that will not be in the final response
DECL|field|fields
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Field names that are OK to include in the response.
comment|// This will include pseudo fields, lucene fields, and matching globs
DECL|field|okFieldNames
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|okFieldNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// The list of explicitly requested fields
comment|// Order is important for CSVResponseWriter
DECL|field|reqFieldNames
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|reqFieldNames
init|=
literal|null
decl_stmt|;
DECL|field|transformer
specifier|protected
name|DocTransformer
name|transformer
decl_stmt|;
DECL|field|_wantsScore
specifier|protected
name|boolean
name|_wantsScore
init|=
literal|false
decl_stmt|;
DECL|field|_wantsAllFields
specifier|protected
name|boolean
name|_wantsAllFields
init|=
literal|false
decl_stmt|;
DECL|method|SolrReturnFields
specifier|public
name|SolrReturnFields
parameter_list|()
block|{
name|_wantsAllFields
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|SolrReturnFields
specifier|public
name|SolrReturnFields
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|this
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|)
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|SolrReturnFields
specifier|public
name|SolrReturnFields
parameter_list|(
name|String
name|fl
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
comment|//    this( (fl==null)?null:SolrPluginUtils.split(fl), req );
if|if
condition|(
name|fl
operator|==
literal|null
condition|)
block|{
name|parseFieldList
argument_list|(
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|fl
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// legacy thing to support fl='  ' => fl=*,score!
comment|// maybe time to drop support for this?
comment|// See ConvertedLegacyTest
name|_wantsScore
operator|=
literal|true
expr_stmt|;
name|_wantsAllFields
operator|=
literal|true
expr_stmt|;
name|transformer
operator|=
operator|new
name|ScoreAugmenter
argument_list|(
name|SCORE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parseFieldList
argument_list|(
operator|new
name|String
index|[]
block|{
name|fl
block|}
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|SolrReturnFields
specifier|public
name|SolrReturnFields
parameter_list|(
name|String
index|[]
name|fl
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|parseFieldList
argument_list|(
name|fl
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|parseFieldList
specifier|private
name|void
name|parseFieldList
parameter_list|(
name|String
index|[]
name|fl
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|_wantsScore
operator|=
literal|false
expr_stmt|;
name|_wantsAllFields
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|fl
operator|==
literal|null
operator|||
name|fl
operator|.
name|length
operator|==
literal|0
operator|||
name|fl
operator|.
name|length
operator|==
literal|1
operator|&&
name|fl
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|_wantsAllFields
operator|=
literal|true
expr_stmt|;
return|return;
block|}
name|NamedList
argument_list|<
name|String
argument_list|>
name|rename
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|DocTransformers
name|augmenters
init|=
operator|new
name|DocTransformers
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fieldList
range|:
name|fl
control|)
block|{
name|add
argument_list|(
name|fieldList
argument_list|,
name|rename
argument_list|,
name|augmenters
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rename
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|from
init|=
name|rename
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|to
init|=
name|rename
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|okFieldNames
operator|.
name|add
argument_list|(
name|to
argument_list|)
expr_stmt|;
name|boolean
name|copy
init|=
operator|(
name|reqFieldNames
operator|!=
literal|null
operator|&&
name|reqFieldNames
operator|.
name|contains
argument_list|(
name|from
argument_list|)
operator|)
decl_stmt|;
if|if
condition|(
operator|!
name|copy
condition|)
block|{
comment|// Check that subsequent copy/rename requests have the field they need to copy
for|for
control|(
name|int
name|j
init|=
name|i
operator|+
literal|1
init|;
name|j
operator|<
name|rename
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|from
operator|.
name|equals
argument_list|(
name|rename
operator|.
name|getName
argument_list|(
name|j
argument_list|)
argument_list|)
condition|)
block|{
name|rename
operator|.
name|setName
argument_list|(
name|j
argument_list|,
name|to
argument_list|)
expr_stmt|;
comment|// copy from the current target
if|if
condition|(
name|reqFieldNames
operator|==
literal|null
condition|)
block|{
name|reqFieldNames
operator|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|reqFieldNames
operator|.
name|add
argument_list|(
name|to
argument_list|)
expr_stmt|;
comment|// don't rename our current target
block|}
block|}
block|}
name|augmenters
operator|.
name|addTransformer
argument_list|(
operator|new
name|RenameFieldTransformer
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|copy
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|augmenters
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|transformer
operator|=
name|augmenters
operator|.
name|getTransformer
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|augmenters
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|transformer
operator|=
name|augmenters
expr_stmt|;
block|}
block|}
comment|// like getId, but also accepts dashes for legacy fields
DECL|method|getFieldName
specifier|public
specifier|static
name|String
name|getFieldName
parameter_list|(
name|StrParser
name|sp
parameter_list|)
block|{
name|sp
operator|.
name|eatws
argument_list|()
expr_stmt|;
name|int
name|id_start
init|=
name|sp
operator|.
name|pos
decl_stmt|;
name|char
name|ch
decl_stmt|;
if|if
condition|(
name|sp
operator|.
name|pos
operator|<
name|sp
operator|.
name|end
operator|&&
operator|(
name|ch
operator|=
name|sp
operator|.
name|val
operator|.
name|charAt
argument_list|(
name|sp
operator|.
name|pos
argument_list|)
operator|)
operator|!=
literal|'$'
operator|&&
name|Character
operator|.
name|isJavaIdentifierStart
argument_list|(
name|ch
argument_list|)
condition|)
block|{
name|sp
operator|.
name|pos
operator|++
expr_stmt|;
while|while
condition|(
name|sp
operator|.
name|pos
operator|<
name|sp
operator|.
name|end
condition|)
block|{
name|ch
operator|=
name|sp
operator|.
name|val
operator|.
name|charAt
argument_list|(
name|sp
operator|.
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Character
operator|.
name|isJavaIdentifierPart
argument_list|(
name|ch
argument_list|)
operator|&&
name|ch
operator|!=
literal|'.'
operator|&&
name|ch
operator|!=
literal|'-'
condition|)
block|{
break|break;
block|}
name|sp
operator|.
name|pos
operator|++
expr_stmt|;
block|}
return|return
name|sp
operator|.
name|val
operator|.
name|substring
argument_list|(
name|id_start
argument_list|,
name|sp
operator|.
name|pos
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|add
specifier|private
name|void
name|add
parameter_list|(
name|String
name|fl
parameter_list|,
name|NamedList
argument_list|<
name|String
argument_list|>
name|rename
parameter_list|,
name|DocTransformers
name|augmenters
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
if|if
condition|(
name|fl
operator|==
literal|null
condition|)
block|{
return|return;
block|}
try|try
block|{
name|StrParser
name|sp
init|=
operator|new
name|StrParser
argument_list|(
name|fl
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|sp
operator|.
name|opt
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sp
operator|.
name|eatws
argument_list|()
expr_stmt|;
if|if
condition|(
name|sp
operator|.
name|pos
operator|>=
name|sp
operator|.
name|end
condition|)
break|break;
name|int
name|start
init|=
name|sp
operator|.
name|pos
decl_stmt|;
comment|// short circuit test for a really simple field name
name|String
name|key
init|=
literal|null
decl_stmt|;
name|String
name|field
init|=
name|getFieldName
argument_list|(
name|sp
argument_list|)
decl_stmt|;
name|char
name|ch
init|=
name|sp
operator|.
name|ch
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sp
operator|.
name|opt
argument_list|(
literal|':'
argument_list|)
condition|)
block|{
comment|// this was a key, not a field name
name|key
operator|=
name|field
expr_stmt|;
name|field
operator|=
literal|null
expr_stmt|;
name|sp
operator|.
name|eatws
argument_list|()
expr_stmt|;
name|start
operator|=
name|sp
operator|.
name|pos
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|Character
operator|.
name|isWhitespace
argument_list|(
name|ch
argument_list|)
operator|||
name|ch
operator|==
literal|','
operator|||
name|ch
operator|==
literal|0
condition|)
block|{
name|addField
argument_list|(
name|field
argument_list|,
name|key
argument_list|,
name|augmenters
argument_list|,
literal|false
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// an invalid field name... reset the position pointer to retry
name|sp
operator|.
name|pos
operator|=
name|start
expr_stmt|;
name|field
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
comment|// we read "key : "
name|field
operator|=
name|sp
operator|.
name|getId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|ch
operator|=
name|sp
operator|.
name|ch
argument_list|()
expr_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
operator|&&
operator|(
name|Character
operator|.
name|isWhitespace
argument_list|(
name|ch
argument_list|)
operator|||
name|ch
operator|==
literal|','
operator|||
name|ch
operator|==
literal|0
operator|)
condition|)
block|{
name|rename
operator|.
name|add
argument_list|(
name|field
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|addField
argument_list|(
name|field
argument_list|,
name|key
argument_list|,
name|augmenters
argument_list|,
literal|false
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// an invalid field name... reset the position pointer to retry
name|sp
operator|.
name|pos
operator|=
name|start
expr_stmt|;
name|field
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
comment|// We didn't find a simple name, so let's see if it's a globbed field name.
comment|// Globbing only works with field names of the recommended form (roughly like java identifiers)
name|field
operator|=
name|sp
operator|.
name|getGlobbedId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|ch
operator|=
name|sp
operator|.
name|ch
argument_list|()
expr_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
operator|&&
operator|(
name|Character
operator|.
name|isWhitespace
argument_list|(
name|ch
argument_list|)
operator|||
name|ch
operator|==
literal|','
operator|||
name|ch
operator|==
literal|0
operator|)
condition|)
block|{
comment|// "*" looks and acts like a glob, but we give it special treatment
if|if
condition|(
literal|"*"
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|_wantsAllFields
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|globs
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
comment|// an invalid glob
name|sp
operator|.
name|pos
operator|=
name|start
expr_stmt|;
block|}
name|String
name|funcStr
init|=
name|sp
operator|.
name|val
operator|.
name|substring
argument_list|(
name|start
argument_list|)
decl_stmt|;
comment|// Is it an augmenter of the form [augmenter_name foo=1 bar=myfield]?
comment|// This is identical to localParams syntax except it uses [] instead of {!}
if|if
condition|(
name|funcStr
operator|.
name|startsWith
argument_list|(
literal|"["
argument_list|)
condition|)
block|{
name|ModifiableSolrParams
name|augmenterParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|int
name|end
init|=
name|QueryParsing
operator|.
name|parseLocalParams
argument_list|(
name|funcStr
argument_list|,
literal|0
argument_list|,
name|augmenterParams
argument_list|,
name|req
operator|.
name|getParams
argument_list|()
argument_list|,
literal|"["
argument_list|,
literal|']'
argument_list|)
decl_stmt|;
name|sp
operator|.
name|pos
operator|+=
name|end
expr_stmt|;
comment|// [foo] is short for [type=foo] in localParams syntax
name|String
name|augmenterName
init|=
name|augmenterParams
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
name|augmenterParams
operator|.
name|remove
argument_list|(
literal|"type"
argument_list|)
expr_stmt|;
name|String
name|disp
init|=
name|key
decl_stmt|;
if|if
condition|(
name|disp
operator|==
literal|null
condition|)
block|{
name|disp
operator|=
literal|'['
operator|+
name|augmenterName
operator|+
literal|']'
expr_stmt|;
block|}
name|TransformerFactory
name|factory
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getTransformerFactory
argument_list|(
name|augmenterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|factory
operator|!=
literal|null
condition|)
block|{
name|DocTransformer
name|t
init|=
name|factory
operator|.
name|create
argument_list|(
name|disp
argument_list|,
name|augmenterParams
argument_list|,
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|_wantsAllFields
condition|)
block|{
name|String
index|[]
name|extra
init|=
name|t
operator|.
name|getExtraRequestFields
argument_list|()
decl_stmt|;
if|if
condition|(
name|extra
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|f
range|:
name|extra
control|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
comment|// also request this field from IndexSearcher
block|}
block|}
block|}
name|augmenters
operator|.
name|addTransformer
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//throw new SolrException(ErrorCode.BAD_REQUEST, "Unknown DocTransformer: "+augmenterName);
block|}
name|addField
argument_list|(
name|field
argument_list|,
name|disp
argument_list|,
name|augmenters
argument_list|,
literal|true
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// let's try it as a function instead
name|QParser
name|parser
init|=
name|QParser
operator|.
name|getParser
argument_list|(
name|funcStr
argument_list|,
name|FunctionQParserPlugin
operator|.
name|NAME
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
literal|null
decl_stmt|;
name|ValueSource
name|vs
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|parser
operator|instanceof
name|FunctionQParser
condition|)
block|{
name|FunctionQParser
name|fparser
init|=
operator|(
name|FunctionQParser
operator|)
name|parser
decl_stmt|;
name|fparser
operator|.
name|setParseMultipleSources
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|fparser
operator|.
name|setParseToEnd
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|q
operator|=
name|fparser
operator|.
name|getQuery
argument_list|()
expr_stmt|;
if|if
condition|(
name|fparser
operator|.
name|localParams
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fparser
operator|.
name|valFollowedParams
condition|)
block|{
comment|// need to find the end of the function query via the string parser
name|int
name|leftOver
init|=
name|fparser
operator|.
name|sp
operator|.
name|end
operator|-
name|fparser
operator|.
name|sp
operator|.
name|pos
decl_stmt|;
name|sp
operator|.
name|pos
operator|=
name|sp
operator|.
name|end
operator|-
name|leftOver
expr_stmt|;
comment|// reset our parser to the same amount of leftover
block|}
else|else
block|{
comment|// the value was via the "v" param in localParams, so we need to find
comment|// the end of the local params themselves to pick up where we left off
name|sp
operator|.
name|pos
operator|=
name|start
operator|+
name|fparser
operator|.
name|localParamsEnd
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// need to find the end of the function query via the string parser
name|int
name|leftOver
init|=
name|fparser
operator|.
name|sp
operator|.
name|end
operator|-
name|fparser
operator|.
name|sp
operator|.
name|pos
decl_stmt|;
name|sp
operator|.
name|pos
operator|=
name|sp
operator|.
name|end
operator|-
name|leftOver
expr_stmt|;
comment|// reset our parser to the same amount of leftover
block|}
block|}
else|else
block|{
comment|// A QParser that's not for function queries.
comment|// It must have been specified via local params.
name|q
operator|=
name|parser
operator|.
name|getQuery
argument_list|()
expr_stmt|;
assert|assert
name|parser
operator|.
name|getLocalParams
argument_list|()
operator|!=
literal|null
assert|;
name|sp
operator|.
name|pos
operator|=
name|start
operator|+
name|parser
operator|.
name|localParamsEnd
expr_stmt|;
block|}
name|funcStr
operator|=
name|sp
operator|.
name|val
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|sp
operator|.
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|q
operator|instanceof
name|FunctionQuery
condition|)
block|{
name|vs
operator|=
operator|(
operator|(
name|FunctionQuery
operator|)
name|q
operator|)
operator|.
name|getValueSource
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|vs
operator|=
operator|new
name|QueryValueSource
argument_list|(
name|q
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
name|SolrParams
name|localParams
init|=
name|parser
operator|.
name|getLocalParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|localParams
operator|!=
literal|null
condition|)
block|{
name|key
operator|=
name|localParams
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
name|key
operator|=
name|funcStr
expr_stmt|;
block|}
name|addField
argument_list|(
name|funcStr
argument_list|,
name|key
argument_list|,
name|augmenters
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|augmenters
operator|.
name|addTransformer
argument_list|(
operator|new
name|ValueSourceAugmenter
argument_list|(
name|key
argument_list|,
name|parser
argument_list|,
name|vs
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SyntaxError
name|e
parameter_list|)
block|{
comment|// try again, simple rules for a field name with no whitespace
name|sp
operator|.
name|pos
operator|=
name|start
expr_stmt|;
name|field
operator|=
name|sp
operator|.
name|getSimpleString
argument_list|()
expr_stmt|;
if|if
condition|(
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldOrNull
argument_list|(
name|field
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// OK, it was an oddly named field
name|addField
argument_list|(
name|field
argument_list|,
name|key
argument_list|,
name|augmenters
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
name|rename
operator|.
name|add
argument_list|(
name|field
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
block|}
else|else
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
literal|"Error parsing fieldname: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// end try as function
block|}
comment|// end for(;;)
block|}
catch|catch
parameter_list|(
name|SyntaxError
name|e
parameter_list|)
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
literal|"Error parsing fieldname"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|addField
specifier|private
name|void
name|addField
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|key
parameter_list|,
name|DocTransformers
name|augmenters
parameter_list|,
name|boolean
name|isPseudoField
parameter_list|)
block|{
if|if
condition|(
name|reqFieldNames
operator|==
literal|null
condition|)
block|{
name|reqFieldNames
operator|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
name|reqFieldNames
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|reqFieldNames
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isPseudoField
condition|)
block|{
comment|// fields is returned by getLuceneFieldNames(), to be used to select which real fields
comment|// to return, so pseudo-fields should not be added
name|fields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|okFieldNames
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|okFieldNames
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
comment|// a valid field name
if|if
condition|(
name|SCORE
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|_wantsScore
operator|=
literal|true
expr_stmt|;
name|String
name|disp
init|=
operator|(
name|key
operator|==
literal|null
operator|)
condition|?
name|field
else|:
name|key
decl_stmt|;
name|augmenters
operator|.
name|addTransformer
argument_list|(
operator|new
name|ScoreAugmenter
argument_list|(
name|disp
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLuceneFieldNames
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getLuceneFieldNames
parameter_list|()
block|{
return|return
name|getLuceneFieldNames
argument_list|(
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getLuceneFieldNames
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getLuceneFieldNames
parameter_list|(
name|boolean
name|ignoreWantsAll
parameter_list|)
block|{
if|if
condition|(
name|ignoreWantsAll
condition|)
return|return
name|fields
return|;
else|else
return|return
operator|(
name|_wantsAllFields
operator|||
name|fields
operator|.
name|isEmpty
argument_list|()
operator|)
condition|?
literal|null
else|:
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|getRequestedFieldNames
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getRequestedFieldNames
parameter_list|()
block|{
if|if
condition|(
name|_wantsAllFields
operator|||
name|reqFieldNames
operator|==
literal|null
operator|||
name|reqFieldNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|reqFieldNames
return|;
block|}
annotation|@
name|Override
DECL|method|hasPatternMatching
specifier|public
name|boolean
name|hasPatternMatching
parameter_list|()
block|{
return|return
operator|!
name|globs
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|wantsField
specifier|public
name|boolean
name|wantsField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|_wantsAllFields
operator|||
name|okFieldNames
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
for|for
control|(
name|String
name|s
range|:
name|globs
control|)
block|{
comment|// TODO something better?
if|if
condition|(
name|FilenameUtils
operator|.
name|wildcardMatch
argument_list|(
name|name
argument_list|,
name|s
argument_list|)
condition|)
block|{
name|okFieldNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
comment|// Don't calculate it again
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|wantsAllFields
specifier|public
name|boolean
name|wantsAllFields
parameter_list|()
block|{
return|return
name|_wantsAllFields
return|;
block|}
annotation|@
name|Override
DECL|method|wantsScore
specifier|public
name|boolean
name|wantsScore
parameter_list|()
block|{
return|return
name|_wantsScore
return|;
block|}
annotation|@
name|Override
DECL|method|getTransformer
specifier|public
name|DocTransformer
name|getTransformer
parameter_list|()
block|{
return|return
name|transformer
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
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"SolrReturnFields=("
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"globs="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|globs
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",fields="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",okFieldNames="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|okFieldNames
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",reqFieldNames="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|reqFieldNames
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",transformer="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|transformer
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",wantsScore="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|_wantsScore
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",wantsAllFields="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|_wantsAllFields
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

