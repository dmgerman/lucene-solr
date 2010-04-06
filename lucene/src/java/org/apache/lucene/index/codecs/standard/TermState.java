begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs.standard
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
operator|.
name|standard
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
comment|// for javadocs
end_comment

begin_comment
comment|/**  * Holds all state required for {@link StandardPostingsReader}  * to produce a {@link DocsEnum} without re-seeking the  * terms dict.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|TermState
specifier|public
class|class
name|TermState
implements|implements
name|Cloneable
block|{
DECL|field|ord
specifier|public
name|long
name|ord
decl_stmt|;
comment|// ord for this term
DECL|field|filePointer
specifier|public
name|long
name|filePointer
decl_stmt|;
comment|// fp into the terms dict primary file (_X.tis)
DECL|field|docFreq
specifier|public
name|int
name|docFreq
decl_stmt|;
comment|// how many docs have this term
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|TermState
name|other
parameter_list|)
block|{
name|ord
operator|=
name|other
operator|.
name|ord
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
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|cnse
parameter_list|)
block|{
comment|// should not happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|cnse
argument_list|)
throw|;
block|}
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
literal|"tis.fp="
operator|+
name|filePointer
operator|+
literal|" docFreq="
operator|+
name|docFreq
operator|+
literal|" ord="
operator|+
name|ord
return|;
block|}
block|}
end_class

end_unit

