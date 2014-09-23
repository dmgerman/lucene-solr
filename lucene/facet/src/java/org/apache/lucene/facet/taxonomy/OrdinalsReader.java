begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.taxonomy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
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
name|IntsRef
import|;
end_import

begin_comment
comment|/** Provides per-document ordinals. */
end_comment

begin_class
DECL|class|OrdinalsReader
specifier|public
specifier|abstract
class|class
name|OrdinalsReader
block|{
comment|/** Returns ordinals for documents in one segment. */
DECL|class|OrdinalsSegmentReader
specifier|public
specifier|static
specifier|abstract
class|class
name|OrdinalsSegmentReader
block|{
comment|/** Get the ordinals for this document.  ordinals.offset      *  must always be 0! */
DECL|method|get
specifier|public
specifier|abstract
name|void
name|get
parameter_list|(
name|int
name|doc
parameter_list|,
name|IntsRef
name|ordinals
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Default constructor. */
DECL|method|OrdinalsSegmentReader
specifier|public
name|OrdinalsSegmentReader
parameter_list|()
block|{     }
block|}
comment|/** Default constructor. */
DECL|method|OrdinalsReader
specifier|public
name|OrdinalsReader
parameter_list|()
block|{   }
comment|/** Set current atomic reader. */
DECL|method|getReader
specifier|public
specifier|abstract
name|OrdinalsSegmentReader
name|getReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns the indexed field name this {@code    *  OrdinalsReader} is reading from. */
DECL|method|getIndexFieldName
specifier|public
specifier|abstract
name|String
name|getIndexFieldName
parameter_list|()
function_decl|;
block|}
end_class

end_unit

