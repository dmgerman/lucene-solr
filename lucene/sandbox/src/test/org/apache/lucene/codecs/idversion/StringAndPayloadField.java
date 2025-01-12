begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.idversion
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|idversion
package|;
end_package

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
name|PayloadAttribute
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|FieldType
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
name|IndexOptions
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
comment|// TODO: can we take a BytesRef token instead?
end_comment

begin_comment
comment|/** Produces a single String token from the provided value, with the provided payload. */
end_comment

begin_class
DECL|class|StringAndPayloadField
class|class
name|StringAndPayloadField
extends|extends
name|Field
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
DECL|field|payload
specifier|private
specifier|final
name|BytesRef
name|payload
decl_stmt|;
DECL|method|StringAndPayloadField
specifier|public
name|StringAndPayloadField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|this
operator|.
name|payload
operator|=
name|payload
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|TokenStream
name|reuse
parameter_list|)
block|{
name|SingleTokenWithPayloadTokenStream
name|ts
decl_stmt|;
if|if
condition|(
name|reuse
operator|instanceof
name|SingleTokenWithPayloadTokenStream
condition|)
block|{
name|ts
operator|=
operator|(
name|SingleTokenWithPayloadTokenStream
operator|)
name|reuse
expr_stmt|;
block|}
else|else
block|{
name|ts
operator|=
operator|new
name|SingleTokenWithPayloadTokenStream
argument_list|()
expr_stmt|;
block|}
name|ts
operator|.
name|setValue
argument_list|(
operator|(
name|String
operator|)
name|fieldsData
argument_list|,
name|payload
argument_list|)
expr_stmt|;
return|return
name|ts
return|;
block|}
DECL|class|SingleTokenWithPayloadTokenStream
specifier|static
specifier|final
class|class
name|SingleTokenWithPayloadTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|termAttribute
specifier|private
specifier|final
name|CharTermAttribute
name|termAttribute
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|payloadAttribute
specifier|private
specifier|final
name|PayloadAttribute
name|payloadAttribute
init|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|used
specifier|private
name|boolean
name|used
init|=
literal|false
decl_stmt|;
DECL|field|value
specifier|private
name|String
name|value
init|=
literal|null
decl_stmt|;
DECL|field|payload
specifier|private
name|BytesRef
name|payload
decl_stmt|;
comment|/** Sets the string value. */
DECL|method|setValue
name|void
name|setValue
parameter_list|(
name|String
name|value
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|payload
operator|=
name|payload
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|used
condition|)
block|{
return|return
literal|false
return|;
block|}
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAttribute
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|payloadAttribute
operator|.
name|setPayload
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|used
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|used
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|value
operator|=
literal|null
expr_stmt|;
name|payload
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

