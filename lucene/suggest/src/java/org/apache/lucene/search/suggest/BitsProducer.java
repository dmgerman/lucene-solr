begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
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
name|Bits
import|;
end_import

begin_comment
comment|/** A producer of {@link Bits} per segment. */
end_comment

begin_class
DECL|class|BitsProducer
specifier|public
specifier|abstract
class|class
name|BitsProducer
block|{
comment|/** Sole constructor, typically invoked by sub-classes. */
DECL|method|BitsProducer
specifier|protected
name|BitsProducer
parameter_list|()
block|{}
comment|/** Return {@link Bits} for the given leaf. The returned instance must    *  be non-null and have a {@link Bits#length() length} equal to    *  {@link LeafReader#maxDoc() maxDoc}. */
DECL|method|getBits
specifier|public
specifier|abstract
name|Bits
name|getBits
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

