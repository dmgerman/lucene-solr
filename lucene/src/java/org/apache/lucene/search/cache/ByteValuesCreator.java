begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.cache
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|cache
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|DocsEnum
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
name|IndexReader
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
name|MultiFields
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
name|Terms
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
name|TermsEnum
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
name|DocIdSetIterator
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
name|FieldCache
operator|.
name|ByteParser
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
name|CachedArray
operator|.
name|ByteValues
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
name|Bits
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
name|OpenBitSet
import|;
end_import

begin_class
DECL|class|ByteValuesCreator
specifier|public
class|class
name|ByteValuesCreator
extends|extends
name|CachedArrayCreator
argument_list|<
name|ByteValues
argument_list|>
block|{
DECL|field|parser
specifier|protected
name|ByteParser
name|parser
decl_stmt|;
DECL|method|ByteValuesCreator
specifier|public
name|ByteValuesCreator
parameter_list|(
name|String
name|field
parameter_list|,
name|ByteParser
name|parser
parameter_list|,
name|int
name|options
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
block|}
DECL|method|ByteValuesCreator
specifier|public
name|ByteValuesCreator
parameter_list|(
name|String
name|field
parameter_list|,
name|ByteParser
name|parser
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getArrayType
specifier|public
name|Class
name|getArrayType
parameter_list|()
block|{
return|return
name|Byte
operator|.
name|class
return|;
block|}
comment|//--------------------------------------------------------------------------------
comment|//--------------------------------------------------------------------------------
annotation|@
name|Override
DECL|method|create
specifier|public
name|ByteValues
name|create
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|validate
argument_list|(
operator|new
name|ByteValues
argument_list|()
argument_list|,
name|reader
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ByteValues
name|validate
parameter_list|(
name|ByteValues
name|entry
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|hasOption
argument_list|(
name|OPTION_CACHE_VALUES
argument_list|)
condition|)
block|{
name|ok
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|values
operator|==
literal|null
condition|)
block|{
name|fillByteValues
argument_list|(
name|entry
argument_list|,
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|hasOption
argument_list|(
name|OPTION_CACHE_BITS
argument_list|)
condition|)
block|{
name|ok
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|valid
operator|==
literal|null
condition|)
block|{
name|fillValidBits
argument_list|(
name|entry
argument_list|,
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|ok
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"the config must cache values and/or bits"
argument_list|)
throw|;
block|}
return|return
name|entry
return|;
block|}
DECL|method|fillByteValues
specifier|protected
name|void
name|fillByteValues
parameter_list|(
name|ByteValues
name|vals
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|parser
operator|==
literal|null
condition|)
block|{
name|parser
operator|=
name|FieldCache
operator|.
name|DEFAULT_BYTE_PARSER
expr_stmt|;
block|}
name|assertSameParserAndResetCounts
argument_list|(
name|vals
argument_list|,
name|parser
argument_list|)
expr_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|vals
operator|.
name|values
operator|=
operator|new
name|byte
index|[
name|maxDoc
index|]
expr_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|final
name|Bits
name|delDocs
init|=
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|OpenBitSet
name|validBits
init|=
operator|(
name|hasOption
argument_list|(
name|OPTION_CACHE_BITS
argument_list|)
operator|)
condition|?
operator|new
name|OpenBitSet
argument_list|(
name|maxDoc
argument_list|)
else|:
literal|null
decl_stmt|;
name|DocsEnum
name|docs
init|=
literal|null
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
break|break;
block|}
specifier|final
name|byte
name|termval
init|=
name|parser
operator|.
name|parseByte
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|docs
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|delDocs
argument_list|,
name|docs
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|docID
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|vals
operator|.
name|values
index|[
name|docID
index|]
operator|=
name|termval
expr_stmt|;
name|vals
operator|.
name|numDocs
operator|++
expr_stmt|;
if|if
condition|(
name|validBits
operator|!=
literal|null
condition|)
block|{
name|validBits
operator|.
name|set
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
block|}
name|vals
operator|.
name|numTerms
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|FieldCache
operator|.
name|StopFillCacheException
name|stop
parameter_list|)
block|{}
if|if
condition|(
name|vals
operator|.
name|valid
operator|==
literal|null
condition|)
block|{
name|vals
operator|.
name|valid
operator|=
name|checkMatchAllBits
argument_list|(
name|delDocs
argument_list|,
name|validBits
argument_list|,
name|vals
operator|.
name|numDocs
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|vals
operator|.
name|valid
operator|==
literal|null
operator|&&
name|vals
operator|.
name|numDocs
operator|<
literal|1
condition|)
block|{
name|vals
operator|.
name|valid
operator|=
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

