begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|TokenStream
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

begin_comment
comment|// TODO: how to handle versioning here...?
end_comment

begin_comment
comment|// TODO: we need to break out separate StoredField...
end_comment

begin_comment
comment|/** Represents a single field for indexing.  IndexWriter  *  consumes Iterable<IndexableField> as a document.  *  *  @lucene.experimental */
end_comment

begin_interface
DECL|interface|IndexableField
specifier|public
interface|interface
name|IndexableField
extends|extends
name|GeneralField
block|{
comment|/**    * Creates the TokenStream used for indexing this field.  If appropriate,    * implementations should use the given Analyzer to create the TokenStreams.    *    * @param analyzer Analyzer that should be used to create the TokenStreams from    * @return TokenStream value for indexing the document.  Should always return    *         a non-null value if the field is to be indexed    * @throws IOException Can be thrown while creating the TokenStream    */
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Field boost (you must pre-multiply in any doc boost). */
DECL|method|boost
specifier|public
name|float
name|boost
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

