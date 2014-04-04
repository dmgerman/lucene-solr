begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|Cell
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
name|Attribute
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
name|AttributeImpl
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
name|AttributeReflector
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
name|AttributeSource
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
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * A TokenStream used internally by {@link org.apache.lucene.spatial.prefix.PrefixTreeStrategy}.  *  * This is highly modelled after {@link org.apache.lucene.analysis.NumericTokenStream}.  *  * If there is demand for it to be public; it could be made to be.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|CellTokenStream
class|class
name|CellTokenStream
extends|extends
name|TokenStream
block|{
DECL|interface|CellTermAttribute
specifier|private
interface|interface
name|CellTermAttribute
extends|extends
name|Attribute
block|{
DECL|method|getCell
name|Cell
name|getCell
parameter_list|()
function_decl|;
DECL|method|setCell
name|void
name|setCell
parameter_list|(
name|Cell
name|cell
parameter_list|)
function_decl|;
comment|//TODO one day deprecate this once we have better encodings
DECL|method|getOmitLeafByte
name|boolean
name|getOmitLeafByte
parameter_list|()
function_decl|;
DECL|method|setOmitLeafByte
name|void
name|setOmitLeafByte
parameter_list|(
name|boolean
name|b
parameter_list|)
function_decl|;
block|}
comment|// just a wrapper to prevent adding CTA
DECL|class|CellAttributeFactory
specifier|private
specifier|static
specifier|final
class|class
name|CellAttributeFactory
extends|extends
name|AttributeSource
operator|.
name|AttributeFactory
block|{
DECL|field|delegate
specifier|private
specifier|final
name|AttributeSource
operator|.
name|AttributeFactory
name|delegate
decl_stmt|;
DECL|method|CellAttributeFactory
name|CellAttributeFactory
parameter_list|(
name|AttributeSource
operator|.
name|AttributeFactory
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createAttributeInstance
specifier|public
name|AttributeImpl
name|createAttributeInstance
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|)
block|{
if|if
condition|(
name|CharTermAttribute
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|attClass
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"CellTokenStream does not support CharTermAttribute."
argument_list|)
throw|;
return|return
name|delegate
operator|.
name|createAttributeInstance
argument_list|(
name|attClass
argument_list|)
return|;
block|}
block|}
DECL|class|CellTermAttributeImpl
specifier|private
specifier|static
specifier|final
class|class
name|CellTermAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|CellTermAttribute
implements|,
name|TermToBytesRefAttribute
block|{
DECL|field|bytes
specifier|private
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|cell
specifier|private
name|Cell
name|cell
decl_stmt|;
DECL|field|omitLeafByte
specifier|private
name|boolean
name|omitLeafByte
decl_stmt|;
comment|//false by default (whether there's a leaf byte or not)
annotation|@
name|Override
DECL|method|getCell
specifier|public
name|Cell
name|getCell
parameter_list|()
block|{
return|return
name|cell
return|;
block|}
annotation|@
name|Override
DECL|method|getOmitLeafByte
specifier|public
name|boolean
name|getOmitLeafByte
parameter_list|()
block|{
return|return
name|omitLeafByte
return|;
block|}
annotation|@
name|Override
DECL|method|setCell
specifier|public
name|void
name|setCell
parameter_list|(
name|Cell
name|cell
parameter_list|)
block|{
name|this
operator|.
name|cell
operator|=
name|cell
expr_stmt|;
name|omitLeafByte
operator|=
literal|false
expr_stmt|;
comment|//reset
block|}
annotation|@
name|Override
DECL|method|setOmitLeafByte
specifier|public
name|void
name|setOmitLeafByte
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
name|omitLeafByte
operator|=
name|b
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
comment|// this attribute has no contents to clear!
comment|// we keep it untouched as it's fully controlled by outer class.
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
specifier|final
name|CellTermAttribute
name|a
init|=
operator|(
name|CellTermAttribute
operator|)
name|target
decl_stmt|;
name|a
operator|.
name|setCell
argument_list|(
name|cell
argument_list|)
expr_stmt|;
name|a
operator|.
name|setOmitLeafByte
argument_list|(
name|omitLeafByte
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fillBytesRef
specifier|public
name|int
name|fillBytesRef
parameter_list|()
block|{
if|if
condition|(
name|omitLeafByte
condition|)
name|cell
operator|.
name|getTokenBytesNoLeaf
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
else|else
name|cell
operator|.
name|getTokenBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
return|return
name|bytes
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getBytesRef
specifier|public
name|BytesRef
name|getBytesRef
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|reflectWith
specifier|public
name|void
name|reflectWith
parameter_list|(
name|AttributeReflector
name|reflector
parameter_list|)
block|{
name|fillBytesRef
argument_list|()
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|,
literal|"bytes"
argument_list|,
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|CellTokenStream
specifier|public
name|CellTokenStream
parameter_list|()
block|{
name|this
argument_list|(
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
argument_list|)
expr_stmt|;
block|}
DECL|method|CellTokenStream
specifier|public
name|CellTokenStream
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|CellAttributeFactory
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setCells
specifier|public
name|CellTokenStream
name|setCells
parameter_list|(
name|Iterator
argument_list|<
name|Cell
argument_list|>
name|iter
parameter_list|)
block|{
name|this
operator|.
name|iter
operator|=
name|iter
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|iter
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"call setCells() before usage"
argument_list|)
throw|;
name|cellAtt
operator|.
name|setCell
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|cellAtt
operator|.
name|setOmitLeafByte
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Outputs the token of a cell, and if its a leaf, outputs it again with the leaf byte. */
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|iter
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"call setCells() before usage"
argument_list|)
throw|;
comment|// this will only clear all other attributes in this TokenStream
name|clearAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|cellAtt
operator|.
name|getOmitLeafByte
argument_list|()
condition|)
block|{
name|cellAtt
operator|.
name|setOmitLeafByte
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|//get next
if|if
condition|(
operator|!
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
return|return
literal|false
return|;
name|cellAtt
operator|.
name|setCell
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|cellAtt
operator|.
name|getCell
argument_list|()
operator|.
name|isLeaf
argument_list|()
condition|)
name|cellAtt
operator|.
name|setOmitLeafByte
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|{
name|addAttributeImpl
argument_list|(
operator|new
name|CellTermAttributeImpl
argument_list|()
argument_list|)
expr_stmt|;
comment|//because non-public constructor
block|}
comment|//members
DECL|field|cellAtt
specifier|private
specifier|final
name|CellTermAttribute
name|cellAtt
init|=
name|addAttribute
argument_list|(
name|CellTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//TODO support position increment, and TypeAttribute
DECL|field|iter
specifier|private
name|Iterator
argument_list|<
name|Cell
argument_list|>
name|iter
init|=
literal|null
decl_stmt|;
comment|// null means not initialized
block|}
end_class

end_unit

