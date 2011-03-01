begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.collation.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|collation
operator|.
name|tokenattributes
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|tokenattributes
operator|.
name|CharTermAttributeImpl
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
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|RawCollationKey
import|;
end_import

begin_comment
comment|/**  * Extension of {@link CharTermAttributeImpl} that encodes the term  * text as a binary Unicode collation key instead of as UTF-8 bytes.  */
end_comment

begin_class
DECL|class|ICUCollatedTermAttributeImpl
specifier|public
class|class
name|ICUCollatedTermAttributeImpl
extends|extends
name|CharTermAttributeImpl
block|{
DECL|field|collator
specifier|private
specifier|final
name|Collator
name|collator
decl_stmt|;
DECL|field|key
specifier|private
specifier|final
name|RawCollationKey
name|key
init|=
operator|new
name|RawCollationKey
argument_list|()
decl_stmt|;
comment|/**    * Create a new ICUCollatedTermAttributeImpl    * @param collator Collation key generator    */
DECL|method|ICUCollatedTermAttributeImpl
specifier|public
name|ICUCollatedTermAttributeImpl
parameter_list|(
name|Collator
name|collator
parameter_list|)
block|{
comment|// clone the collator: see http://userguide.icu-project.org/collation/architecture
try|try
block|{
name|this
operator|.
name|collator
operator|=
operator|(
name|Collator
operator|)
name|collator
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toBytesRef
specifier|public
name|int
name|toBytesRef
parameter_list|(
name|BytesRef
name|target
parameter_list|)
block|{
name|collator
operator|.
name|getRawCollationKey
argument_list|(
name|toString
argument_list|()
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|target
operator|.
name|bytes
operator|=
name|key
operator|.
name|bytes
expr_stmt|;
name|target
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|target
operator|.
name|length
operator|=
name|key
operator|.
name|size
expr_stmt|;
return|return
name|target
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

