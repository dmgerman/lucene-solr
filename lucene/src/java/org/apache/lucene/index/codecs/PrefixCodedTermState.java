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
name|index
operator|.
name|DocsEnum
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
name|OrdTermState
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
name|TermState
import|;
end_import

begin_comment
comment|/**  * Holds all state required for {@link PostingsReaderBase}  * to produce a {@link DocsEnum} without re-seeking the  * terms dict.  */
end_comment

begin_class
DECL|class|PrefixCodedTermState
specifier|public
class|class
name|PrefixCodedTermState
extends|extends
name|OrdTermState
block|{
DECL|field|docFreq
specifier|public
name|int
name|docFreq
decl_stmt|;
comment|// how many docs have this term
DECL|field|filePointer
specifier|public
name|long
name|filePointer
decl_stmt|;
comment|// fp into the terms dict primary file (_X.tis)
DECL|field|totalTermFreq
specifier|public
name|long
name|totalTermFreq
decl_stmt|;
comment|// total number of occurrences of this term
annotation|@
name|Override
DECL|method|copyFrom
specifier|public
name|void
name|copyFrom
parameter_list|(
name|TermState
name|_other
parameter_list|)
block|{
assert|assert
name|_other
operator|instanceof
name|PrefixCodedTermState
operator|:
literal|"can not copy from "
operator|+
name|_other
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
assert|;
name|PrefixCodedTermState
name|other
init|=
operator|(
name|PrefixCodedTermState
operator|)
name|_other
decl_stmt|;
name|super
operator|.
name|copyFrom
argument_list|(
name|_other
argument_list|)
expr_stmt|;
name|filePointer
operator|=
name|other
operator|.
name|filePointer
expr_stmt|;
name|docFreq
operator|=
name|other
operator|.
name|docFreq
expr_stmt|;
name|totalTermFreq
operator|=
name|other
operator|.
name|totalTermFreq
expr_stmt|;
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
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|"[ord="
operator|+
name|ord
operator|+
literal|", tis.filePointer="
operator|+
name|filePointer
operator|+
literal|", docFreq="
operator|+
name|docFreq
operator|+
literal|", totalTermFreq="
operator|+
name|totalTermFreq
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

