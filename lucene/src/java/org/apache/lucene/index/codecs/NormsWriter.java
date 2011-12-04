begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
comment|// simple api just for now before switching to docvalues apis
end_comment

begin_class
DECL|class|NormsWriter
specifier|public
specifier|abstract
class|class
name|NormsWriter
implements|implements
name|Closeable
block|{
comment|// TODO: I think IW should set info.normValueType from Similarity,
comment|// and then this method just returns DocValuesConsumer
DECL|method|startField
specifier|public
specifier|abstract
name|void
name|startField
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeNorm
specifier|public
specifier|abstract
name|void
name|writeNorm
parameter_list|(
name|byte
name|norm
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|finish
specifier|public
specifier|abstract
name|void
name|finish
parameter_list|(
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

