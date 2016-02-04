begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|util
operator|.
name|Accountable
import|;
end_import

begin_comment
comment|/** Abstract API that produces field normalization values  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|NormsProducer
specifier|public
specifier|abstract
class|class
name|NormsProducer
implements|implements
name|Closeable
implements|,
name|Accountable
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|NormsProducer
specifier|protected
name|NormsProducer
parameter_list|()
block|{}
comment|/** Returns {@link NumericDocValues} for this field.    *  The returned instance need not be thread-safe: it will only be    *  used by a single thread. */
DECL|method|getNorms
specifier|public
specifier|abstract
name|NumericDocValues
name|getNorms
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * Checks consistency of this producer    *<p>    * Note that this may be costly in terms of I/O, e.g.     * may involve computing a checksum value against large data files.    * @lucene.internal    */
DECL|method|checkIntegrity
specifier|public
specifier|abstract
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**     * Returns an instance optimized for merging.    *<p>    * The default implementation returns {@code this} */
DECL|method|getMergeInstance
specifier|public
name|NormsProducer
name|getMergeInstance
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

