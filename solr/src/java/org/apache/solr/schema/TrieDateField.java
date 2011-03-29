begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|noggit
operator|.
name|CharArr
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
name|analysis
operator|.
name|CharFilterFactory
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
name|analysis
operator|.
name|TokenFilterFactory
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
name|analysis
operator|.
name|TokenizerChain
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
name|analysis
operator|.
name|TrieTokenizerFactory
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
name|function
operator|.
name|*
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
name|lucene
operator|.
name|document
operator|.
name|Fieldable
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
name|lucene
operator|.
name|search
operator|.
name|FieldCache
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
name|NumericRangeQuery
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
name|cache
operator|.
name|CachedArrayCreator
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
name|cache
operator|.
name|LongValuesCreator
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
name|util
operator|.
name|BytesRef
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
name|util
operator|.
name|NumericUtils
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
name|util
operator|.
name|TrieFieldHelper
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
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|TrieDateField
specifier|public
class|class
name|TrieDateField
extends|extends
name|DateField
block|{
DECL|field|precisionStepArg
specifier|protected
name|int
name|precisionStepArg
init|=
name|TrieField
operator|.
name|DEFAULT_PRECISION_STEP
decl_stmt|;
comment|// the one passed in or defaulted
DECL|field|precisionStep
specifier|protected
name|int
name|precisionStep
init|=
name|precisionStepArg
decl_stmt|;
comment|// normalized
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
name|String
name|p
init|=
name|args
operator|.
name|remove
argument_list|(
literal|"precisionStep"
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|precisionStepArg
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
comment|// normalize the precisionStep
name|precisionStep
operator|=
name|precisionStepArg
expr_stmt|;
if|if
condition|(
name|precisionStep
operator|<=
literal|0
operator|||
name|precisionStep
operator|>=
literal|64
condition|)
name|precisionStep
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
name|CharFilterFactory
index|[]
name|filterFactories
init|=
operator|new
name|CharFilterFactory
index|[
literal|0
index|]
decl_stmt|;
name|TokenFilterFactory
index|[]
name|tokenFilterFactories
init|=
operator|new
name|TokenFilterFactory
index|[
literal|0
index|]
decl_stmt|;
name|analyzer
operator|=
operator|new
name|TokenizerChain
argument_list|(
name|filterFactories
argument_list|,
operator|new
name|TrieTokenizerFactory
argument_list|(
name|TrieField
operator|.
name|TrieTypes
operator|.
name|DATE
argument_list|,
name|precisionStep
argument_list|)
argument_list|,
name|tokenFilterFactories
argument_list|)
expr_stmt|;
comment|// for query time we only need one token, so we use the biggest possible precisionStep:
name|queryAnalyzer
operator|=
operator|new
name|TokenizerChain
argument_list|(
name|filterFactories
argument_list|,
operator|new
name|TrieTokenizerFactory
argument_list|(
name|TrieField
operator|.
name|TrieTypes
operator|.
name|DATE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
name|tokenFilterFactories
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Date
name|toObject
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
name|byte
index|[]
name|arr
init|=
name|f
operator|.
name|getBinaryValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|arr
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|TrieField
operator|.
name|badFieldString
argument_list|(
name|f
argument_list|)
argument_list|)
throw|;
return|return
operator|new
name|Date
argument_list|(
name|TrieFieldHelper
operator|.
name|toLong
argument_list|(
name|arr
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Object
name|toObject
parameter_list|(
name|SchemaField
name|sf
parameter_list|,
name|BytesRef
name|term
parameter_list|)
block|{
return|return
operator|new
name|Date
argument_list|(
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|term
argument_list|)
argument_list|)
return|;
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
name|top
parameter_list|)
block|{
name|field
operator|.
name|checkSortability
argument_list|()
expr_stmt|;
name|int
name|flags
init|=
name|CachedArrayCreator
operator|.
name|CACHE_VALUES_AND_BITS
decl_stmt|;
name|boolean
name|sortMissingLast
init|=
name|field
operator|.
name|sortMissingLast
argument_list|()
decl_stmt|;
name|boolean
name|sortMissingFirst
init|=
name|field
operator|.
name|sortMissingFirst
argument_list|()
decl_stmt|;
name|Object
name|missingValue
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sortMissingLast
condition|)
block|{
name|missingValue
operator|=
name|top
condition|?
name|Long
operator|.
name|MIN_VALUE
else|:
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sortMissingFirst
condition|)
block|{
name|missingValue
operator|=
name|top
condition|?
name|Long
operator|.
name|MAX_VALUE
else|:
name|Long
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
return|return
operator|new
name|SortField
argument_list|(
operator|new
name|LongValuesCreator
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|FieldCache
operator|.
name|NUMERIC_UTILS_LONG_PARSER
argument_list|,
name|flags
argument_list|)
argument_list|,
name|top
argument_list|)
operator|.
name|setMissingValue
argument_list|(
name|missingValue
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|QParser
name|parser
parameter_list|)
block|{
name|field
operator|.
name|checkFieldCacheSource
argument_list|(
name|parser
argument_list|)
expr_stmt|;
return|return
operator|new
name|TrieDateFieldSource
argument_list|(
operator|new
name|LongValuesCreator
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|FieldCache
operator|.
name|NUMERIC_UTILS_LONG_PARSER
argument_list|,
name|CachedArrayCreator
operator|.
name|CACHE_VALUES_AND_BITS
argument_list|)
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
name|Fieldable
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|arr
init|=
name|f
operator|.
name|getBinaryValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|arr
operator|==
literal|null
condition|)
block|{
name|writer
operator|.
name|writeStr
argument_list|(
name|name
argument_list|,
name|TrieField
operator|.
name|badFieldString
argument_list|(
name|f
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return;
block|}
name|writer
operator|.
name|writeDate
argument_list|(
name|name
argument_list|,
operator|new
name|Date
argument_list|(
name|TrieFieldHelper
operator|.
name|toLong
argument_list|(
name|arr
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isTokenized
specifier|public
name|boolean
name|isTokenized
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * @return the precisionStep used to index values into the field    */
DECL|method|getPrecisionStep
specifier|public
name|int
name|getPrecisionStep
parameter_list|()
block|{
return|return
name|precisionStepArg
return|;
block|}
annotation|@
name|Override
DECL|method|storedToReadable
specifier|public
name|String
name|storedToReadable
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
return|return
name|toExternal
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readableToIndexed
specifier|public
name|String
name|readableToIndexed
parameter_list|(
name|String
name|val
parameter_list|)
block|{
comment|// TODO: Numeric should never be handled as String, that may break in future lucene versions! Change to use BytesRef for term texts!
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
name|NumericUtils
operator|.
name|BUF_SIZE_LONG
argument_list|)
decl_stmt|;
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|super
operator|.
name|parseMath
argument_list|(
literal|null
argument_list|,
name|val
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|bytes
operator|.
name|utf8ToString
argument_list|()
return|;
block|}
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
return|return
name|readableToIndexed
argument_list|(
name|val
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
name|byte
index|[]
name|arr
init|=
name|f
operator|.
name|getBinaryValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|arr
operator|==
literal|null
condition|)
return|return
name|TrieField
operator|.
name|badFieldString
argument_list|(
name|f
argument_list|)
return|;
return|return
name|super
operator|.
name|toExternal
argument_list|(
operator|new
name|Date
argument_list|(
name|TrieFieldHelper
operator|.
name|toLong
argument_list|(
name|arr
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|indexedToReadable
specifier|public
name|String
name|indexedToReadable
parameter_list|(
name|String
name|_indexedForm
parameter_list|)
block|{
specifier|final
name|BytesRef
name|indexedForm
init|=
operator|new
name|BytesRef
argument_list|(
name|_indexedForm
argument_list|)
decl_stmt|;
return|return
name|super
operator|.
name|toExternal
argument_list|(
operator|new
name|Date
argument_list|(
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|indexedForm
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|indexedToReadable
specifier|public
name|void
name|indexedToReadable
parameter_list|(
name|BytesRef
name|input
parameter_list|,
name|CharArr
name|out
parameter_list|)
block|{
name|String
name|ext
init|=
name|super
operator|.
name|toExternal
argument_list|(
operator|new
name|Date
argument_list|(
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|ext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|storedToIndexed
specifier|public
name|String
name|storedToIndexed
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
comment|// TODO: optimize to remove redundant string conversion
return|return
name|readableToIndexed
argument_list|(
name|storedToReadable
argument_list|(
name|f
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRangeQuery
specifier|public
name|Query
name|getRangeQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|min
parameter_list|,
name|String
name|max
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|field
argument_list|,
name|min
operator|==
literal|null
condition|?
literal|null
else|:
name|super
operator|.
name|parseMath
argument_list|(
literal|null
argument_list|,
name|min
argument_list|)
argument_list|,
name|max
operator|==
literal|null
condition|?
literal|null
else|:
name|super
operator|.
name|parseMath
argument_list|(
literal|null
argument_list|,
name|max
argument_list|)
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRangeQuery
specifier|public
name|Query
name|getRangeQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|sf
parameter_list|,
name|Date
name|min
parameter_list|,
name|Date
name|max
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
name|int
name|ps
init|=
name|precisionStep
decl_stmt|;
name|Query
name|query
init|=
name|NumericRangeQuery
operator|.
name|newLongRange
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|,
name|ps
argument_list|,
name|min
operator|==
literal|null
condition|?
literal|null
else|:
name|min
operator|.
name|getTime
argument_list|()
argument_list|,
name|max
operator|==
literal|null
condition|?
literal|null
else|:
name|max
operator|.
name|getTime
argument_list|()
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
decl_stmt|;
return|return
name|query
return|;
block|}
block|}
end_class

end_unit

