begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Fields
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
name|SegmentWriteState
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_comment
comment|/**   * Abstract API that consumes terms, doc, freq, prox, offset and  * payloads postings.  Concrete implementations of this  * actually do "something" with the postings (write it into  * the index in a specific format).  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FieldsConsumer
specifier|public
specifier|abstract
class|class
name|FieldsConsumer
implements|implements
name|Closeable
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|FieldsConsumer
specifier|protected
name|FieldsConsumer
parameter_list|()
block|{   }
comment|// TODO: can we somehow compute stats for you...?
comment|// TODO: maybe we should factor out "limited" (only
comment|// iterables, no counts/stats) base classes from
comment|// Fields/Terms/Docs/AndPositions?
comment|/** Write all fields, terms and postings.  This the "pull"    *  API, allowing you to iterate more than once over the    *  postings, somewhat analogous to using a DOM API to    *  traverse an XML tree.    *    *<p><b>Notes</b>:    *    *<ul>    *<li> You must compute index statistics,    *         including each Term's docFreq and totalTermFreq,    *         as well as the summary sumTotalTermFreq,    *         sumTotalDocFreq and docCount.    *    *<li> You must skip terms that have no docs and    *         fields that have no terms, even though the provided    *         Fields API will expose them; this typically    *         requires lazily writing the field or term until    *         you've actually seen the first term or    *         document.    *    *<li> The provided Fields instance is limited: you    *         cannot call any methods that return    *         statistics/counts; you cannot pass a non-null    *         live docs when pulling docs/positions enums.    *</ul>    */
DECL|method|write
specifier|public
specifier|abstract
name|void
name|write
parameter_list|(
name|Fields
name|fields
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|// NOTE: strange but necessary so javadocs linting is happy:
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

