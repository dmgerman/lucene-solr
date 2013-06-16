begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.temp
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|temp
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|TermState
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
name|store
operator|.
name|ByteArrayDataInput
import|;
end_import

begin_comment
comment|/**  * Holds all state required for {@link PostingsReaderBase}  * to produce a {@link DocsEnum} without re-seeking the  * terms dict.  */
end_comment

begin_class
DECL|class|TempTermState
specifier|public
class|class
name|TempTermState
extends|extends
name|TermState
block|{
comment|/** how many docs have this term */
DECL|field|docFreq
specifier|public
name|int
name|docFreq
decl_stmt|;
comment|/** total number of occurrences of this term */
DECL|field|totalTermFreq
specifier|public
name|long
name|totalTermFreq
decl_stmt|;
comment|/** the term's ord in the current block */
DECL|field|termBlockOrd
specifier|public
name|int
name|termBlockOrd
decl_stmt|;
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|TempTermState
specifier|protected
name|TempTermState
parameter_list|()
block|{   }
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
name|TempTermState
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
name|TempTermState
name|other
init|=
operator|(
name|TempTermState
operator|)
name|_other
decl_stmt|;
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
name|termBlockOrd
operator|=
name|other
operator|.
name|termBlockOrd
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
literal|"docFreq="
operator|+
name|docFreq
operator|+
literal|" totalTermFreq="
operator|+
name|totalTermFreq
operator|+
literal|" termBlockOrd="
operator|+
name|termBlockOrd
return|;
block|}
block|}
end_class

end_unit

