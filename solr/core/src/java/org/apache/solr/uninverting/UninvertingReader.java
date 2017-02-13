begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.uninverting
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|uninverting
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
name|ArrayList
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
name|lucene
operator|.
name|document
operator|.
name|BinaryDocValuesField
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|NumericDocValuesField
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|SortedDocValuesField
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|SortedNumericDocValuesField
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
name|SortedSetDocValuesField
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|StringField
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|BinaryDocValues
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
name|DirectoryReader
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
name|DocValuesType
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
name|FieldInfo
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
name|FieldInfos
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
name|FilterDirectoryReader
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
name|FilterLeafReader
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
name|IndexOptions
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
name|LeafReader
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
name|NumericDocValues
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
name|SortedDocValues
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
name|SortedSetDocValues
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
name|RamUsageEstimator
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
name|uninverting
operator|.
name|FieldCache
operator|.
name|CacheEntry
import|;
end_import

begin_comment
comment|/**  * A FilterReader that exposes<i>indexed</i> values as if they also had  * docvalues.  *<p>  * This is accomplished by "inverting the inverted index" or "uninversion".  *<p>  * The uninversion process happens lazily: upon the first request for the   * field's docvalues (e.g. via {@link org.apache.lucene.index.LeafReader#getNumericDocValues(String)}   * or similar), it will create the docvalues on-the-fly if needed and cache it,  * based on the core cache key of the wrapped LeafReader.  */
end_comment

begin_class
DECL|class|UninvertingReader
specifier|public
class|class
name|UninvertingReader
extends|extends
name|FilterLeafReader
block|{
comment|/**    * Specifies the type of uninversion to apply for the field.     */
DECL|enum|Type
specifier|public
specifier|static
enum|enum
name|Type
block|{
comment|/**       * Single-valued Integer, (e.g. indexed with {@link org.apache.lucene.document.IntPoint})      *<p>      * Fields with this type act as if they were indexed with      * {@link NumericDocValuesField}.      */
DECL|enum constant|INTEGER_POINT
name|INTEGER_POINT
block|,
comment|/**       * Single-valued Integer, (e.g. indexed with {@link org.apache.lucene.document.LongPoint})      *<p>      * Fields with this type act as if they were indexed with      * {@link NumericDocValuesField}.      */
DECL|enum constant|LONG_POINT
name|LONG_POINT
block|,
comment|/**       * Single-valued Integer, (e.g. indexed with {@link org.apache.lucene.document.FloatPoint})      *<p>      * Fields with this type act as if they were indexed with      * {@link NumericDocValuesField}.      */
DECL|enum constant|FLOAT_POINT
name|FLOAT_POINT
block|,
comment|/**       * Single-valued Integer, (e.g. indexed with {@link org.apache.lucene.document.DoublePoint})      *<p>      * Fields with this type act as if they were indexed with      * {@link NumericDocValuesField}.      */
DECL|enum constant|DOUBLE_POINT
name|DOUBLE_POINT
block|,
comment|/**       * Single-valued Integer, (e.g. indexed with {@link org.apache.lucene.legacy.LegacyIntField})      *<p>      * Fields with this type act as if they were indexed with      * {@link NumericDocValuesField}.      * @deprecated Index with points and use {@link #INTEGER_POINT} instead.      */
DECL|enum constant|Deprecated
annotation|@
name|Deprecated
DECL|enum constant|LEGACY_INTEGER
name|LEGACY_INTEGER
block|,
comment|/**       * Single-valued Long, (e.g. indexed with {@link org.apache.lucene.legacy.LegacyLongField})      *<p>      * Fields with this type act as if they were indexed with      * {@link NumericDocValuesField}.      * @deprecated Index with points and use {@link #LONG_POINT} instead.      */
DECL|enum constant|Deprecated
annotation|@
name|Deprecated
DECL|enum constant|LEGACY_LONG
name|LEGACY_LONG
block|,
comment|/**       * Single-valued Float, (e.g. indexed with {@link org.apache.lucene.legacy.LegacyFloatField})      *<p>      * Fields with this type act as if they were indexed with      * {@link NumericDocValuesField}.      * @deprecated Index with points and use {@link #FLOAT_POINT} instead.      */
DECL|enum constant|Deprecated
annotation|@
name|Deprecated
DECL|enum constant|LEGACY_FLOAT
name|LEGACY_FLOAT
block|,
comment|/**       * Single-valued Double, (e.g. indexed with {@link org.apache.lucene.legacy.LegacyDoubleField})      *<p>      * Fields with this type act as if they were indexed with      * {@link NumericDocValuesField}.      * @deprecated Index with points and use {@link #DOUBLE_POINT} instead.      */
DECL|enum constant|Deprecated
annotation|@
name|Deprecated
DECL|enum constant|LEGACY_DOUBLE
name|LEGACY_DOUBLE
block|,
comment|/**       * Single-valued Binary, (e.g. indexed with {@link StringField})       *<p>      * Fields with this type act as if they were indexed with      * {@link BinaryDocValuesField}.      */
DECL|enum constant|BINARY
name|BINARY
block|,
comment|/**       * Single-valued Binary, (e.g. indexed with {@link StringField})       *<p>      * Fields with this type act as if they were indexed with      * {@link SortedDocValuesField}.      */
DECL|enum constant|SORTED
name|SORTED
block|,
comment|/**       * Multi-valued Binary, (e.g. indexed with {@link StringField})       *<p>      * Fields with this type act as if they were indexed with      * {@link SortedSetDocValuesField}.      */
DECL|enum constant|SORTED_SET_BINARY
name|SORTED_SET_BINARY
block|,
comment|/**       * Multi-valued Integer, (e.g. indexed with {@link org.apache.lucene.legacy.LegacyIntField})      *<p>      * Fields with this type act as if they were indexed with      * {@link SortedSetDocValuesField}.      */
DECL|enum constant|SORTED_SET_INTEGER
name|SORTED_SET_INTEGER
block|,
comment|/**       * Multi-valued Float, (e.g. indexed with {@link org.apache.lucene.legacy.LegacyFloatField})      *<p>      * Fields with this type act as if they were indexed with      * {@link SortedSetDocValuesField}.      */
DECL|enum constant|SORTED_SET_FLOAT
name|SORTED_SET_FLOAT
block|,
comment|/**       * Multi-valued Long, (e.g. indexed with {@link org.apache.lucene.legacy.LegacyLongField})      *<p>      * Fields with this type act as if they were indexed with      * {@link SortedSetDocValuesField}.      */
DECL|enum constant|SORTED_SET_LONG
name|SORTED_SET_LONG
block|,
comment|/**       * Multi-valued Double, (e.g. indexed with {@link org.apache.lucene.legacy.LegacyDoubleField})      *<p>      * Fields with this type act as if they were indexed with      * {@link SortedSetDocValuesField}.      */
DECL|enum constant|SORTED_SET_DOUBLE
name|SORTED_SET_DOUBLE
block|,
comment|/**       * Multi-valued Integer, (e.g. indexed with {@link org.apache.lucene.document.IntPoint})      *<p>      * Fields with this type act as if they were indexed with      * {@link SortedNumericDocValuesField}.      */
DECL|enum constant|SORTED_INTEGER
name|SORTED_INTEGER
block|,
comment|/**       * Multi-valued Float, (e.g. indexed with {@link org.apache.lucene.document.FloatPoint})      *<p>      * Fields with this type act as if they were indexed with      * {@link SortedNumericDocValuesField}.      */
DECL|enum constant|SORTED_FLOAT
name|SORTED_FLOAT
block|,
comment|/**       * Multi-valued Long, (e.g. indexed with {@link org.apache.lucene.document.LongPoint})      *<p>      * Fields with this type act as if they were indexed with      * {@link SortedNumericDocValuesField}.      */
DECL|enum constant|SORTED_LONG
name|SORTED_LONG
block|,
comment|/**       * Multi-valued Double, (e.g. indexed with {@link org.apache.lucene.document.DoublePoint})      *<p>      * Fields with this type act as if they were indexed with      * {@link SortedNumericDocValuesField}.      */
DECL|enum constant|SORTED_DOUBLE
name|SORTED_DOUBLE
block|}
comment|/**    * Wraps a provided DirectoryReader. Note that for convenience, the returned reader    * can be used normally (e.g. passed to {@link DirectoryReader#openIfChanged(DirectoryReader)})    * and so on.     */
DECL|method|wrap
specifier|public
specifier|static
name|DirectoryReader
name|wrap
parameter_list|(
name|DirectoryReader
name|in
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Type
argument_list|>
name|mapping
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|UninvertingDirectoryReader
argument_list|(
name|in
argument_list|,
name|mapping
argument_list|)
return|;
block|}
DECL|class|UninvertingDirectoryReader
specifier|static
class|class
name|UninvertingDirectoryReader
extends|extends
name|FilterDirectoryReader
block|{
DECL|field|mapping
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Type
argument_list|>
name|mapping
decl_stmt|;
DECL|method|UninvertingDirectoryReader
specifier|public
name|UninvertingDirectoryReader
parameter_list|(
name|DirectoryReader
name|in
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Type
argument_list|>
name|mapping
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|,
operator|new
name|FilterDirectoryReader
operator|.
name|SubReaderWrapper
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|LeafReader
name|wrap
parameter_list|(
name|LeafReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|UninvertingReader
argument_list|(
name|reader
argument_list|,
name|mapping
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|mapping
operator|=
name|mapping
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWrapDirectoryReader
specifier|protected
name|DirectoryReader
name|doWrapDirectoryReader
parameter_list|(
name|DirectoryReader
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|UninvertingDirectoryReader
argument_list|(
name|in
argument_list|,
name|mapping
argument_list|)
return|;
block|}
block|}
DECL|field|mapping
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Type
argument_list|>
name|mapping
decl_stmt|;
DECL|field|fieldInfos
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
comment|/**     * Create a new UninvertingReader with the specified mapping     *<p>    * Expert: This should almost never be used. Use {@link #wrap(DirectoryReader, Map)}    * instead.    *      * @lucene.internal    */
DECL|method|UninvertingReader
specifier|public
name|UninvertingReader
parameter_list|(
name|LeafReader
name|in
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Type
argument_list|>
name|mapping
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|mapping
operator|=
name|mapping
expr_stmt|;
name|ArrayList
argument_list|<
name|FieldInfo
argument_list|>
name|filteredInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|in
operator|.
name|getFieldInfos
argument_list|()
control|)
block|{
name|DocValuesType
name|type
init|=
name|fi
operator|.
name|getDocValuesType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|DocValuesType
operator|.
name|NONE
condition|)
block|{
name|Type
name|t
init|=
name|mapping
operator|.
name|get
argument_list|(
name|fi
operator|.
name|name
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
name|t
operator|==
name|Type
operator|.
name|INTEGER_POINT
operator|||
name|t
operator|==
name|Type
operator|.
name|LONG_POINT
operator|||
name|t
operator|==
name|Type
operator|.
name|FLOAT_POINT
operator|||
name|t
operator|==
name|Type
operator|.
name|DOUBLE_POINT
condition|)
block|{
comment|// type uses points
if|if
condition|(
name|fi
operator|.
name|getPointDimensionCount
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
block|}
else|else
block|{
comment|// type uses inverted index
if|if
condition|(
name|fi
operator|.
name|getIndexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
continue|continue;
block|}
block|}
switch|switch
condition|(
name|t
condition|)
block|{
case|case
name|INTEGER_POINT
case|:
case|case
name|LONG_POINT
case|:
case|case
name|FLOAT_POINT
case|:
case|case
name|DOUBLE_POINT
case|:
case|case
name|LEGACY_INTEGER
case|:
case|case
name|LEGACY_LONG
case|:
case|case
name|LEGACY_FLOAT
case|:
case|case
name|LEGACY_DOUBLE
case|:
name|type
operator|=
name|DocValuesType
operator|.
name|NUMERIC
expr_stmt|;
break|break;
case|case
name|BINARY
case|:
name|type
operator|=
name|DocValuesType
operator|.
name|BINARY
expr_stmt|;
break|break;
case|case
name|SORTED
case|:
name|type
operator|=
name|DocValuesType
operator|.
name|SORTED
expr_stmt|;
break|break;
case|case
name|SORTED_SET_BINARY
case|:
case|case
name|SORTED_SET_INTEGER
case|:
case|case
name|SORTED_SET_FLOAT
case|:
case|case
name|SORTED_SET_LONG
case|:
case|case
name|SORTED_SET_DOUBLE
case|:
name|type
operator|=
name|DocValuesType
operator|.
name|SORTED_SET
expr_stmt|;
break|break;
case|case
name|SORTED_INTEGER
case|:
case|case
name|SORTED_FLOAT
case|:
case|case
name|SORTED_LONG
case|:
case|case
name|SORTED_DOUBLE
case|:
name|type
operator|=
name|DocValuesType
operator|.
name|SORTED_NUMERIC
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
block|}
name|filteredInfos
operator|.
name|add
argument_list|(
operator|new
name|FieldInfo
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|fi
operator|.
name|number
argument_list|,
name|fi
operator|.
name|hasVectors
argument_list|()
argument_list|,
name|fi
operator|.
name|omitsNorms
argument_list|()
argument_list|,
name|fi
operator|.
name|hasPayloads
argument_list|()
argument_list|,
name|fi
operator|.
name|getIndexOptions
argument_list|()
argument_list|,
name|type
argument_list|,
name|fi
operator|.
name|getDocValuesGen
argument_list|()
argument_list|,
name|fi
operator|.
name|attributes
argument_list|()
argument_list|,
name|fi
operator|.
name|getPointDimensionCount
argument_list|()
argument_list|,
name|fi
operator|.
name|getPointNumBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fieldInfos
operator|=
operator|new
name|FieldInfos
argument_list|(
name|filteredInfos
operator|.
name|toArray
argument_list|(
operator|new
name|FieldInfo
index|[
name|filteredInfos
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFieldInfos
specifier|public
name|FieldInfos
name|getFieldInfos
parameter_list|()
block|{
return|return
name|fieldInfos
return|;
block|}
annotation|@
name|Override
DECL|method|getNumericDocValues
specifier|public
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|NumericDocValues
name|values
init|=
name|super
operator|.
name|getNumericDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
return|return
name|values
return|;
block|}
name|Type
name|v
init|=
name|getType
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|v
condition|)
block|{
case|case
name|INTEGER_POINT
case|:
return|return
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getNumerics
argument_list|(
name|in
argument_list|,
name|field
argument_list|,
name|FieldCache
operator|.
name|INT_POINT_PARSER
argument_list|)
return|;
case|case
name|FLOAT_POINT
case|:
return|return
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getNumerics
argument_list|(
name|in
argument_list|,
name|field
argument_list|,
name|FieldCache
operator|.
name|FLOAT_POINT_PARSER
argument_list|)
return|;
case|case
name|LONG_POINT
case|:
return|return
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getNumerics
argument_list|(
name|in
argument_list|,
name|field
argument_list|,
name|FieldCache
operator|.
name|LONG_POINT_PARSER
argument_list|)
return|;
case|case
name|DOUBLE_POINT
case|:
return|return
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getNumerics
argument_list|(
name|in
argument_list|,
name|field
argument_list|,
name|FieldCache
operator|.
name|DOUBLE_POINT_PARSER
argument_list|)
return|;
case|case
name|LEGACY_INTEGER
case|:
return|return
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getNumerics
argument_list|(
name|in
argument_list|,
name|field
argument_list|,
name|FieldCache
operator|.
name|LEGACY_INT_PARSER
argument_list|)
return|;
case|case
name|LEGACY_FLOAT
case|:
return|return
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getNumerics
argument_list|(
name|in
argument_list|,
name|field
argument_list|,
name|FieldCache
operator|.
name|LEGACY_FLOAT_PARSER
argument_list|)
return|;
case|case
name|LEGACY_LONG
case|:
return|return
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getNumerics
argument_list|(
name|in
argument_list|,
name|field
argument_list|,
name|FieldCache
operator|.
name|LEGACY_LONG_PARSER
argument_list|)
return|;
case|case
name|LEGACY_DOUBLE
case|:
return|return
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getNumerics
argument_list|(
name|in
argument_list|,
name|field
argument_list|,
name|FieldCache
operator|.
name|LEGACY_DOUBLE_PARSER
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getBinaryDocValues
specifier|public
name|BinaryDocValues
name|getBinaryDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|BinaryDocValues
name|values
init|=
name|in
operator|.
name|getBinaryDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
return|return
name|values
return|;
block|}
name|Type
name|v
init|=
name|getType
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
name|Type
operator|.
name|BINARY
condition|)
block|{
return|return
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTerms
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSortedDocValues
specifier|public
name|SortedDocValues
name|getSortedDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedDocValues
name|values
init|=
name|in
operator|.
name|getSortedDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
return|return
name|values
return|;
block|}
name|Type
name|v
init|=
name|getType
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
name|Type
operator|.
name|SORTED
condition|)
block|{
return|return
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTermsIndex
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSortedSetDocValues
specifier|public
name|SortedSetDocValues
name|getSortedSetDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedSetDocValues
name|values
init|=
name|in
operator|.
name|getSortedSetDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
return|return
name|values
return|;
block|}
name|Type
name|v
init|=
name|getType
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|v
condition|)
block|{
case|case
name|SORTED_SET_INTEGER
case|:
case|case
name|SORTED_SET_FLOAT
case|:
return|return
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getDocTermOrds
argument_list|(
name|in
argument_list|,
name|field
argument_list|,
name|FieldCache
operator|.
name|INT32_TERM_PREFIX
argument_list|)
return|;
case|case
name|SORTED_SET_LONG
case|:
case|case
name|SORTED_SET_DOUBLE
case|:
return|return
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getDocTermOrds
argument_list|(
name|in
argument_list|,
name|field
argument_list|,
name|FieldCache
operator|.
name|INT64_TERM_PREFIX
argument_list|)
return|;
case|case
name|SORTED_SET_BINARY
case|:
return|return
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getDocTermOrds
argument_list|(
name|in
argument_list|,
name|field
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**     * Returns the field's uninversion type, or null     * if the field doesn't exist or doesn't have a mapping.    */
DECL|method|getType
specifier|private
name|Type
name|getType
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|FieldInfo
name|info
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
operator|||
name|info
operator|.
name|getDocValuesType
argument_list|()
operator|==
name|DocValuesType
operator|.
name|NONE
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|mapping
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCoreCacheKey
specifier|public
name|Object
name|getCoreCacheKey
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCoreCacheKey
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCombinedCoreAndDeletesKey
specifier|public
name|Object
name|getCombinedCoreAndDeletesKey
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCombinedCoreAndDeletesKey
argument_list|()
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
return|return
literal|"Uninverting("
operator|+
name|in
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
comment|/**     * Return information about the backing cache    * @lucene.internal     */
DECL|method|getUninvertedStats
specifier|public
specifier|static
name|FieldCacheStats
name|getUninvertedStats
parameter_list|()
block|{
name|CacheEntry
index|[]
name|entries
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getCacheEntries
argument_list|()
decl_stmt|;
name|long
name|totalBytesUsed
init|=
literal|0
decl_stmt|;
name|String
index|[]
name|info
init|=
operator|new
name|String
index|[
name|entries
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|entries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|info
index|[
name|i
index|]
operator|=
name|entries
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
expr_stmt|;
name|totalBytesUsed
operator|+=
name|entries
index|[
name|i
index|]
operator|.
name|getValue
argument_list|()
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
name|String
name|totalSize
init|=
name|RamUsageEstimator
operator|.
name|humanReadableUnits
argument_list|(
name|totalBytesUsed
argument_list|)
decl_stmt|;
return|return
operator|new
name|FieldCacheStats
argument_list|(
name|totalSize
argument_list|,
name|info
argument_list|)
return|;
block|}
DECL|method|getUninvertedStatsSize
specifier|public
specifier|static
name|int
name|getUninvertedStatsSize
parameter_list|()
block|{
return|return
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getCacheEntries
argument_list|()
operator|.
name|length
return|;
block|}
comment|/**    * Return information about the backing cache    * @lucene.internal    */
DECL|class|FieldCacheStats
specifier|public
specifier|static
class|class
name|FieldCacheStats
block|{
DECL|field|totalSize
specifier|public
name|String
name|totalSize
decl_stmt|;
DECL|field|info
specifier|public
name|String
index|[]
name|info
decl_stmt|;
DECL|method|FieldCacheStats
specifier|public
name|FieldCacheStats
parameter_list|(
name|String
name|totalSize
parameter_list|,
name|String
index|[]
name|info
parameter_list|)
block|{
name|this
operator|.
name|totalSize
operator|=
name|totalSize
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

